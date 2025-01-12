package backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jade.core.AID;

/**
 * Clasa MessageHistory gestioneaza istoricul conversatiilor intre agenti.
 * Aceasta salveaza, incarca si permite accesul la mesajele schimbate intre
 * agenti.
 */
public class MessageHistory {

	private Map<String, StringBuilder> conversations = new HashMap<>();
	private static final String HISTORY_FILE = "chat_history.txt";

	/**
	 * Adauga un mesaj la istoricul conversatiei intre doi agenti.
	 * 
	 * @param sender   agentul care trimite mesajul.
	 * @param receiver agentul care primeste mesajul.
	 * @param message  mesajul trimis.
	 */
	public void addMessage(AID sender, AID receiver, String message) {
		String conversationKey = getConversationKey(sender, receiver);
		StringBuilder conversation = conversations.getOrDefault(conversationKey, new StringBuilder());
		conversation.append(sender.getLocalName()).append(": ").append(message).append("\n");
		conversations.put(conversationKey, conversation);

		saveHistoryToFile();
	}

	/**
	 * Obtine istoricul conversatiei dintre doi agenti.
	 * 
	 * @param sender   agentul care a trimis mesajele.
	 * @param receiver agentul care a primit mesajele.
	 * @return istoricul conversatiei dintre cei doi agenti.
	 */
	public String getConversationHistory(AID sender, AID receiver) {
		String conversationKey = getConversationKey(sender, receiver);
		return conversations.getOrDefault(conversationKey, new StringBuilder()).toString();
	}

	/**
	 * Salveaza istoricul conversatiilor intr-un fisier text. Fiecare conversatie
	 * este salvata cu cheia corespunzatoare si continutul mesajelor.
	 */
	private void saveHistoryToFile() {
		try {
			File file = new File(HISTORY_FILE);
			if (!file.exists()) {
				file.createNewFile();
			}

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
				for (Map.Entry<String, StringBuilder> entry : conversations.entrySet()) {
					writer.write(entry.getKey());
					writer.write("\n");
					writer.write(entry.getValue().toString());
					writer.write("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Incarca istoricul conversatiilor din fisier. Fiecare conversatie este citita
	 * si stocata in memoria aplicatiei.
	 */
	public void loadHistoryFromFile() {
		try {
			File file = new File(HISTORY_FILE);
			if (!file.exists()) {
				file.createNewFile();
			}

			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String line;
				StringBuilder conversation = new StringBuilder();
				String currentKey = "";
				while ((line = reader.readLine()) != null) {
					if (line.isEmpty()) {
						continue;
					}
					if (line.contains("-")) {
						if (!conversation.toString().isEmpty()) {
							conversations.put(currentKey, conversation);
						}
						currentKey = line;
						conversation = new StringBuilder();
					} else {
						conversation.append(line).append("\n");
					}
				}
				if (!conversation.toString().isEmpty()) {
					conversations.put(currentKey, conversation);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Genereaza o cheie unica pentru conversatia dintre doi agenti. Cheia este
	 * bazata pe numele local ale agentilor, iar ordinea numelui este importanta
	 * pentru a asigura unicitatea.
	 * 
	 * @param sender   agentul care trimite mesajele.
	 * @param receiver agentul care primeste mesajele.
	 * @return cheia unica pentru conversatia dintre cei doi agenti.
	 */
	private String getConversationKey(AID sender, AID receiver) {
		return sender.getLocalName().compareTo(receiver.getLocalName()) < 0
				? sender.getLocalName() + "-" + receiver.getLocalName()
				: receiver.getLocalName() + "-" + sender.getLocalName();
	}
}
