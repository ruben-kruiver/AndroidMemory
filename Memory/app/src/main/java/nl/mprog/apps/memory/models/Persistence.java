package nl.mprog.apps.memory.models;

// Welk thema
// Welke kaarten (imageIndex, visible, disabled)
// Current level van de game
// Current sequence van de game
//
// de eerste bit is de zichtbaarheid, daarna de most significant nibble shiften
// 00000000 00000000 00000000 00000000 <= Rekening houden met 32 bits cpu
//                                   | zichtbaarheid
//          |                       |  positie van de kaart binnen het speelveld
// |      |                            Index van de kaart binnen het thema (getal van 0 tot 255)

// Bijvoorbeeld:
// 00000011 00000010 00000000 00000001 Kaart nummer 3 uit het thema, 7e kaart van links is zichtbaar

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class Persistence {

    protected ArrayList<Card> cards;

    protected Integer currentMistakes;

    protected Integer currentTime;

    protected Integer currentLevel;

    protected String filename;

    protected Context context;

    protected boolean isLoaded;

    public Persistence(Context context, String filename) {
        this.cards = new ArrayList();
        this.currentTime = 0;
        this.currentMistakes = 0;
        this.currentLevel = 1;
        this.filename = filename;
        this.context = context;
        this.isLoaded = false;
    }

    public ArrayList<Card> getCards() {
        return this.cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public Integer getCurrentMistakes() {
        return this.currentMistakes;
    }

    public void setCurrentMistakes(Integer currentMistakes) {
        this.currentMistakes = currentMistakes;
    }

    public Integer getCurrentLevel() {
        return this.currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getCurrentTime() {
        return this.currentTime;
    }

    public void setCurrentTime(Integer currentTime) {
        this.currentTime = currentTime;
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }

    public void clear() {
        this.currentMistakes = 0;
        this.currentTime = 0;
        this.currentLevel = 1;
        this.cards = new ArrayList<Card>();
    }

    public void load() {
        this.isLoaded = true;
        String basepath = Environment.getExternalStorageDirectory().toString();
        File file = new File(basepath + File.separator + Memory.PERSISTENCE_FOLDER, this.filename);
        String content = this.getStringFromFile(file);

        try {
            Document data = this.loadXMLFromString(content);

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xp = xpf.newXPath();
            String mistakes = xp.evaluate("//persist/currentMistakes/@value", data.getDocumentElement());
            String time = xp.evaluate("//persist/currentTime/@value", data.getDocumentElement());
            String level = xp.evaluate("//persist/currentLevel/@value", data.getDocumentElement());

            this.currentMistakes = Integer.parseInt(mistakes);
            this.currentLevel = Integer.parseInt(level);
            this.currentTime = Integer.parseInt(time);

            // Load the card values
            XPathExpression expr = xp.compile("//persist/cards/*");
            NodeList nodes = (NodeList) expr.evaluate(data, XPathConstants.NODESET);

            Integer[] cardValues = new Integer[nodes.getLength()];

            for (int i = 0; i < nodes.getLength(); i++) {
                Element card = (Element) nodes.item(i);
                cardValues[i] = Integer.parseInt(card.getAttribute("value"));
            }

            this.cards = this.getCardsFromValues(cardValues);

        } catch (Exception e) {
            Log.e("error", "Could not read XML data from persistence file");
        }

    }

    public void store() {
        try {
            String basepath = Environment.getExternalStorageDirectory().toString();

            File folder = new File(basepath, Memory.PERSISTENCE_FOLDER);

            if (!folder.exists()) {
                folder.mkdirs();

                if (!folder.exists()) {
                    Log.e("Error", "Could not create persistence folder");
                }

                return;
            }

            String content = this.createPersistenceContent();

            File file = new File(basepath + File.separator + Memory.PERSISTENCE_FOLDER, this.filename);
            FileOutputStream os = new FileOutputStream(file);
            os.write(content.getBytes(), 0, content.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String createPersistenceContent() {
        String content = "";

        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.newDocument();

            Element root = doc.createElement("persist");
            doc.appendChild(root);

            Element cards = doc.createElement("cards");
            root.appendChild(cards);

            this.addCards(doc, cards);
            this.addNode(doc, root, "currentMistakes", this.currentMistakes.toString());
            this.addNode(doc, root, "currentTime", this.currentTime.toString());
            this.addNode(doc, root, "currentLevel", this.currentLevel.toString());

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            content = writer.getBuffer().toString().replaceAll("\n|\r", "");
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return content;
    }

    protected void addCards(Document doc, Element cardsRoot) {
        Integer[] cardValues = this.getCardValues();

        for (Integer cardValue : cardValues) {
            this.addNode(doc, cardsRoot, "card", cardValue.toString());
        }
    }

    protected void addNode(Document doc, Element root, String nodeName, String value) {
        Element node = doc.createElement(nodeName);
        node.setAttribute("value", value);
        root.appendChild(node);
    }

    /**
     * This method converts the list of cards to an integer value calculated with bitwise operators
     * With this value the
     * @return
     */
    protected Integer[] getCardValues() {
        Integer[] convertedCards = new Integer[this.cards.size()];

        Integer currentIndex = 0;
        for (Card card : this.cards) {
            Integer currentCard = 0;

            // Store the current index
            currentCard ^= card.getImageIndex();

            // Shift the value 23 positions to the left to hold the position index
            currentCard = currentCard << 23;

            // Store the current position
            currentCard ^= currentIndex;

            // Shift the value 1 position to the left to hold the visibility flag
            currentCard = currentCard << 1;

            // Set the visibility flag
            currentCard ^= (card.isVisible() ? 1 : 0);

            convertedCards[currentIndex] = currentCard;

            currentIndex++;
        }

        return convertedCards;
    }

    /**
     * This method creates card from integer values. It is the reverse method of getCardValues
     */
    protected ArrayList<Card> getCardsFromValues(Integer[] cardValues) {
        ArrayList<Card> cards = new ArrayList();
        Card[] temp = new Card[cardValues.length];

        for (Integer value : cardValues) {
            Card card = new Card();

            // Reset the visibility
            card.setVisible((value & 1) == 1);
            // First shift the image index of the value, then the visibility bit
            Integer cardIndex = value << 8;
            cardIndex = cardIndex >> 9;

            // Reset the current image index
            card.setImageIndex((value >> 24));

            temp[cardIndex] = card;
        }

        cards = new ArrayList<Card>(Arrays.asList(temp));
        return cards;
    }


    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (File file) {
        String content = "";
        try {
            FileInputStream fin = new FileInputStream(file);
            content = convertStreamToString(fin);
            fin.close();
        } catch (Exception ex) {
            Log.e("error", "could not read persistence file");
        }

        return content;
    }

    public Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(new ByteArrayInputStream(xml.getBytes()));
    }
}
