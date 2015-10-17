package nl.mprog.apps.memory.model.persistence;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.mprog.apps.memory.basemodel.Game;
import nl.mprog.apps.memory.model.Card;

/**
 * This class is a delegate for the Persistence class. It is responsible for
 * saving the current game and its state to the defined persistence file
 */
public class Keeper {

    protected Game game;

    protected Document document;

    protected Element rootElement;

    protected File persistenceFile;

    public Keeper(File persistenceFile, Game game) {
        this.persistenceFile = persistenceFile;
        this.game = game;
    }

    public void storeGame() {
        try {
            String content = this.createPersistenceContent();
            FileOutputStream os = new FileOutputStream(this.persistenceFile);
            os.write(content.getBytes(), 0, content.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String createPersistenceContent() {
        String content = "";

        try{
            this.loadDocument();

            this.saveGameSettings();

            this.savePlayingCards();

            this.saveReferences();

            content = this.getDocumentContent();
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return content;
    }

    protected void loadDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();
        this.document = parser.newDocument();

        // Create the root node in the document
        this.rootElement = this.document.createElement("persistence");
        this.document.appendChild(this.rootElement);
    }

    protected void saveGameSettings() {
        this.addNode(this.rootElement, "gameType", this.game.getClass().getName());
        this.addNode(this.rootElement, "currentLevel", this.game.getCurrentLevel().toString());
        this.addNode(this.rootElement, "numberOfCardsCorrect", this.game.getNumberOfCardsCorrect().toString());
        this.addNode(this.rootElement, "cardsPerSet", this.game.getCardsPerSet().toString());
        this.addNode(this.rootElement, "maximumMistakes", this.game.getMaximumMistakes().toString());
        this.addNode(this.rootElement, "currentMistakesLimit", this.game.getCurrentMistakesLimit().toString());
        this.addNode(this.rootElement, "currentMistakes", this.game.getCurrentMistakes().toString());
        this.addNode(this.rootElement, "timeLimit", this.game.getTimeLimit().toString());
        this.addNode(this.rootElement, "currentTimelimit", this.game.getCurrentTimelimit().toString());
        this.addNode(this.rootElement, "currentTime", this.game.getCurrentTime().toString());
        this.addNode(this.rootElement, "isLocked", this.game.isLocked().toString());
    }

    protected void savePlayingCards() {
        Element playingCards = this.document.createElement("playingCards");
        this.rootElement.appendChild(playingCards);

        for (Card card : this.game.getPlayingCards()) {
            this.addCard(this.getCardValue(card), playingCards);
        }
    }

    /**
     * Create a bitmask to enable restoring the cards from the persistence context. It will
     * hold the image index of the theme, the disabled flag and the visibility flag
     */
    protected Integer getCardValue(Card card) {
        Integer currentCard = 0;

        // Store the current index
        currentCard ^= card.getImageIndex();

        // Shift the value 1 position to the right and set the disabled flag
        currentCard = currentCard << 1;
        currentCard ^= (card.isDisabled() ? 1 : 0);

        // Shift the value 1 position to the right and set the visibility flag
        currentCard = currentCard << 1;
        currentCard ^= (card.isVisible() ? 1 : 0);

        return currentCard;
    }

    protected void saveReferences() {
        this.setCardReferences(this.game.getCurrentSet(), "currentSet");
        this.setCardReferences(this.game.getFailedSet(), "failedSet");
    }

    protected void setCardReferences(ArrayList<Card> cards, String nodeName){
        Element referenceNode = this.document.createElement(nodeName);
        this.rootElement.appendChild(referenceNode);

        for (Card card : cards) {
            Integer position = this.game.getPlayingCards().indexOf(card);

            if (position != null) {
                this.addCard(position, referenceNode);
            }
        }
    }

    protected void addNode(Element root, String nodeName, String value) {
        Element node = this.document.createElement(nodeName);
        node.setAttribute("value", value);
        root.appendChild(node);
    }

    protected void addCard(Integer cardValue, Element cardsRoot) {
        this.addNode(cardsRoot, "card", cardValue.toString());
    }

    protected String getDocumentContent() throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(this.document), new StreamResult(writer));
        return writer.getBuffer().toString().replaceAll("\n|\r", "");
    }
}
