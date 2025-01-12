package backend;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import frontend.ChatUI;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

/**
 * Agentul de chat care gestioneaza interactiunea cu alti agenti. Extinde clasa
 * {@link Agent} din Jade pentru a realiza comunicarea intre agenti.
 */
public class ChatAgent extends Agent {

	private ChatUI chatUI;
	private List<AID> allAgents = new ArrayList<>();
	private boolean canSendMessage = true;
	private MessageHistory messageHistory;

	/**
	 * Metoda care este apelata la pornirea agentului. Initializeaza interfata
	 * utilizatorului, incarca istoricul mesajelor si inregistreaza agentul in
	 * directory facilitator (DF) si afiseaza un mesaj in consola cand agentul este
	 * pornit.
	 */
	@Override
	protected void setup() {
		System.out.println(getLocalName() + " has started.");

		SwingUtilities.invokeLater(() -> {
			chatUI = new ChatUI(getLocalName(), ChatAgent.this);
			chatUI.setVisible(true);
		});

		messageHistory = new MessageHistory();
		messageHistory.loadHistoryFromFile();
		registerAgentInDF();
		getAllAgents();

		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				ACLMessage msg = receive();
				if (msg != null) {
					String message = msg.getContent();
					String sender = msg.getSender().getLocalName();
					displayMessageToRecipient(sender + ": " + message);
					messageHistory.addMessage(msg.getSender(), getAID(), message);
				} else {
					block();
				}
			}
		});
	}

	/**
	 * Inregistreaza agentul in directory facilitator (DF) pentru a-l face
	 * disponibil pentru alti agenti.
	 */
	private void registerAgentInDF() {
		try {
			DFAgentDescription dfAgentDescription = new DFAgentDescription();
			dfAgentDescription.setName(getAID());
			DFService.register(this, dfAgentDescription);
			System.out.println(getLocalName() + " registered in DF.");
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Obtine toti agentii inregistrati in directory facilitator (DF). Impreuna cu o
	 * logica de retry in cazul in care cautarea nu reuseste din prima.
	 * 
	 * @return o lista cu agentii gasiti in DF.
	 */
	public List<AID> getAllAgents() {
		List<AID> agents = new ArrayList<>();
		int retries = 3;
		int delay = 2000;

		while (retries > 0) {
			try {
				DFAgentDescription template = new DFAgentDescription();
				DFAgentDescription[] result = DFService.search(this, template);

				allAgents.clear();
				for (DFAgentDescription agentDesc : result) {
					allAgents.add(agentDesc.getName());
				}

				agents.addAll(allAgents);
				break;
			} catch (FIPAException e) {
				e.printStackTrace();
				retries--;
				if (retries > 0) {
					try {
						Thread.sleep(delay);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		return agents;
	}

	/**
	 * Trimite un mesaj unui agent specificat. Mesajul este afisat in interfata
	 * utilizatorului si adaugat in istoricul mesajelor.
	 * 
	 * @param messageContent continutul mesajului de trimis.
	 * @param recipient      agentul destinatar al mesajului.
	 */
	public void sendMessage(String messageContent, AID recipient) {
		if (canSendMessage && recipient != null) {
			canSendMessage = false;

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(recipient);
			msg.setContent(messageContent);
			send(msg);

			chatUI.displayMessage("You: " + messageContent);
			messageHistory.addMessage(getAID(), recipient, messageContent);
		}
	}

	/**
	 * Trimite un mesaj utilizand datele din interfata utilizatorului. Dupa
	 * trimiterea mesajului, agentul este setat ca fiind capabil sa trimita un alt
	 * mesaj.
	 * 
	 * @param messageContent continutul mesajului de trimis.
	 * @param recipient      agentul destinatar al mesajului.
	 */
	public void sendMessageFromUI(String messageContent, AID recipient) {
		sendMessage(messageContent, recipient);
		canSendMessage = true;
	}

	/**
	 * Afiseaza un mesaj pentru un destinatar in interfata utilizatorului.
	 * 
	 * @param message mesajul care va fi afisat.
	 */
	public void displayMessageToRecipient(String message) {
		SwingUtilities.invokeLater(() -> chatUI.displayMessage(message));
	}

	/**
	 * Obtine istoricul conversatiei intre agentul curent si un agent destinat.
	 * 
	 * @param recipient agentul destinat al conversatiei.
	 * @return istoricul conversatiei sub forma unui sir de caractere.
	 */
	public String getConversationHistory(AID recipient) {
		return messageHistory.getConversationHistory(getAID(), recipient);
	}

	/**
	 * Actualizeaza istoricul conversatiei in interfata utilizatorului pentru un
	 * agent destinat.
	 * 
	 * @param recipient agentul destinat al conversatiei.
	 */
	public void updateConversationHistory(AID recipient) {
		String history = messageHistory.getConversationHistory(getAID(), recipient);
		chatUI.updateConversationHistory(history);
	}
}
