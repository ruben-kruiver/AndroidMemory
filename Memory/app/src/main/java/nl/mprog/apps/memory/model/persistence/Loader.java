package nl.mprog.apps.memory.model.persistence;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.mprog.apps.memory.basemodel.Game;
import nl.mprog.apps.memory.model.Card;

/**
 * This class is a delegate for the Persistence class. It is responsible for
 * restoring a game from the defined persistence file.
 */
public class Loader {

    protected Game game;

    protected XPath xp;

    protected Element rootElement;

    protected File persistenceFile;

    public Loader(File persistenceFile) {
        this.persistenceFile = persistenceFile;
    }


    public Game reloadGame() throws Exception {
        try {
            // Load the persistence data file into a DOM Document
            Document data = this.loadDocumentFromFile(this.persistenceFile);
            this.rootElement = data.getDocumentElement();

            // Load the XPath parser to parse the DOM Document
            XPathFactory xpf = XPathFactory.newInstance();
            this.xp = xpf.newXPath();

            // Load the game instance as described in the persistence file
            this.loadGame();

            // Load the settings from the persistence file
            this.loadGameSettings();

            // Load the cards and the gamestate references from the persistence file
            this.loadCardsForGame();
        } catch (Exception ex) {
            Log.e("error", "Could not read XML data from persistence file. " + ex.getLocalizedMessage());
            throw ex;
        }

        return this.game;
    }

    protected Document loadDocumentFromFile(File file) throws IOException, ParserConfigurationException, SAXException {
        FileInputStream fin = new FileInputStream(file);
        String content = convertStreamToString(fin);
        fin.close();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(new ByteArrayInputStream(content.getBytes()));
    }

    protected String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    protected void loadGame() throws XPathExpressionException,
            ClassNotFoundException,
            IllegalAccessException,
            InstantiationException {

        String gameType = this.xp.evaluate("//persistence/gameType/@value", this.rootElement);
        this.game = (Game) Class.forName(gameType).newInstance();
    }

    protected void loadGameSettings() throws XPathExpressionException {
        String maximumMistakes = this.xp.evaluate("//persistence/maximumMistakes/@value", this.rootElement);
        this.game.setMaximumMistakes(Integer.parseInt(maximumMistakes));

        String currentMistakesLimit = this.xp.evaluate("//persistence/currentMistakesLimit/@value", this.rootElement);
        this.game.setCurrentMistakesLimit(Integer.parseInt(currentMistakesLimit));

        String currentMistakes = this.xp.evaluate("//persistence/currentMistakes/@value", this.rootElement);
        this.game.setCurrentMistakes(Integer.parseInt(currentMistakes));

        String timeLimit = this.xp.evaluate("//persistence/timeLimit/@value", this.rootElement);
        this.game.setTimeLimit(Integer.parseInt(timeLimit));

        String currentTimelimit = this.xp.evaluate("//persistence/currentTimelimit/@value", this.rootElement);
        this.game.setCurrentTimeLimit(Integer.parseInt(currentTimelimit));

        String currentTime = this.xp.evaluate("//persistence/currentTime/@value", this.rootElement);
        this.game.setCurrentTime(Integer.parseInt(currentTime));

        String currentLevel = this.xp.evaluate("//persistence/currentLevel/@value", this.rootElement);
        this.game.setCurrentLevel(Integer.parseInt(currentLevel));

        String cardsCorrect = this.xp.evaluate("//persistence/numberOfCardsCorrect/@value", this.rootElement);
        this.game.setNumberOfCardsCorrect(Integer.parseInt(cardsCorrect));

        String cardsPerSet = this.xp.evaluate("//persistence/cardsPerSet/@value", this.rootElement);
        this.game.setCardsPerSet(Integer.parseInt(cardsPerSet));

        String isLocked = this.xp.evaluate("//persistence/isLocked/@value", this.rootElement);
        this.game.setLocked(Boolean.parseBoolean(isLocked));
    }

    protected void loadCardsForGame() throws XPathExpressionException {
        this.game.setPlayingCards(this.getCardsFromNode("playingCards"));

        // Also restore the references to the current game state
        this.game.setCurrentSet(this.getCardsFromNode("currentSet"));
        this.game.setFailedSet(this.getCardsFromNode("failedSet"));
    }

    /**
     * This method loads the cards from the DOM Document element defined by nodeName
     * and places them in an ArrayList. Depending on the node it will either load the
     * playing cards or the references to the playingcards in a set to restore the last
     * game state.
     */
    protected ArrayList<Card> getCardsFromNode(String nodeName) throws XPathExpressionException {
        XPathExpression expr = this.xp.compile("//persistence/" + nodeName + "/*");
        NodeList nodes = (NodeList) expr.evaluate(this.rootElement, XPathConstants.NODESET);

        ArrayList<Card> cards = new ArrayList();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element cardNode = (Element) nodes.item(i);
            Integer cardValue = Integer.parseInt(cardNode.getAttribute("value"));

            switch (nodeName) {
                case "playingCards":
                    cards.add(this.getCardFromValue(cardValue)); break;

                case "currentSet":
                case "failedSet":
                    Card card = this.game.getPlayingCards().get(cardValue);

                    if (card != null) {
                        cards.add(card);
                    }
                    break;
            }
        }

        return cards;
    }

    /**
     * This method creates a card from its integer values as calculated by getCardValue in the Keeper
     */
    protected Card getCardFromValue(Integer cardValue) {
        Card card = new Card();

        // Reset the visibility
        card.setVisible((cardValue & 1) == 1);

        // Reset the disabled state
        cardValue = cardValue >> 1;
        card.setDisabled((cardValue & 1) == 1);

        // Get the image index by removing the visibility bit
        card.setImageIndex((cardValue >> 1));
        return card;
    }
}
