package frontend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import backend.ChatAgent;
import jade.core.AID;

/**
 * Clasa ChatUI reprezinta interfata grafica a utilizatorului pentru agentul de
 * chat. Aceasta permite utilizatorului sa trimita si sa primeasca mesaje de la
 * alti agenti si sa vizualizeze istoricul conversatiilor.
 */
public class ChatUI extends JFrame {

	private JTextArea textArea;
	private JTextField textField;
	private JComboBox<AID> agentComboBox;
	private JButton updateButton;
	private ChatAgent agent;
	private JButton sendButton;

	/**
	 * Constructorul principal al clasei ChatUI. Initializeaza interfata
	 * utilizatorului, setand dimensiunile, componentele si evenimentele necesare.
	 * 
	 * @param agentName numele agentului curent.
	 * @param agent     instanta agentului care gestioneaza logica chat-ului.
	 */
	public ChatUI(String agentName, ChatAgent agent) {
		this.agent = agent;

		setTitle("Chat Agent - " + agentName);
		setSize(750, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		// Configurare textarea pentru afisarea mesajelor
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(textArea);

		// Panelul superior, cu lista agentilor si butonul de actualizare
		JPanel topPanel = new JPanel(new BorderLayout());
		agentComboBox = new JComboBox<>();
		updateAgentList(agent.getAllAgents());
		topPanel.add(agentComboBox, BorderLayout.WEST);

		updateButton = new JButton("Update");
		topPanel.add(updateButton, BorderLayout.EAST);

		// Eveniment de selectare a unui agent din comboBox
		agentComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					AID selectedAgent = (AID) agentComboBox.getSelectedItem();
					if (selectedAgent != null) {
						textArea.setText("");
						String history = agent.getConversationHistory(selectedAgent);
						textArea.setText(history);
					}
				}
			}
		});

		// Butonul de actualizare a listei de agenti
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<AID> allAgents = agent.getAllAgents();
				updateAgentList(allAgents);

				AID selectedAgent = (AID) agentComboBox.getSelectedItem();
				if (selectedAgent != null) {
					String history = agent.getConversationHistory(selectedAgent);
					textArea.setText(history);
				}
			}
		});

		// Panelul inferior, cu campul de text pentru mesaj si butonul de trimitere
		JPanel bottomPanel = new JPanel(new BorderLayout());
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(0, 25));
		bottomPanel.add(textField, BorderLayout.CENTER);

		// Eveniment pentru trimiterea mesajului la apasarea Enter
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = textField.getText();
				if (message != null && !message.isEmpty()) {
					AID selectedAgent = (AID) agentComboBox.getSelectedItem();
					if (selectedAgent != null && !selectedAgent.getLocalName().equals(agent.getLocalName())) {
						agent.sendMessageFromUI(message, selectedAgent);
						textField.setText("");
					}
				}
			}
		});

		// Butonul de trimitere a mesajului
		sendButton = new JButton("Send");
		sendButton.setPreferredSize(new Dimension(100, 25));
		bottomPanel.add(sendButton, BorderLayout.EAST);

		// Eveniment pentru trimiterea mesajului la apasarea butonului
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = textField.getText();
				if (message != null && !message.isEmpty()) {
					AID selectedAgent = (AID) agentComboBox.getSelectedItem();
					if (selectedAgent != null && !selectedAgent.getLocalName().equals(agent.getLocalName())) {
						agent.sendMessageFromUI(message, selectedAgent);
						textField.setText("");
					}
				}
			}
		});

		// Adaugarea componentelor in fereastra principala
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(topPanel, BorderLayout.NORTH);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}

	/**
	 * Afiseaza un mesaj in fereastra de chat.
	 * 
	 * @param message mesajul de afisat in fereastra.
	 */
	public void displayMessage(String message) {
		textArea.append(message + "\n");
	}

	/**
	 * Actualizeaza lista de agenti din comboBox-ul interfetei, adaugand agentii
	 * disponibili.
	 * 
	 * @param agents lista agentilor care vor aparea in comboBox.
	 */
	public void updateAgentList(List<AID> agents) {
		agentComboBox.removeAllItems();

		for (AID agent : agents) {
			if (!agent.getLocalName().equals(this.agent.getLocalName())) {
				agentComboBox.addItem(agent);
			}
		}

		AID selectedAgent = (AID) agentComboBox.getSelectedItem();
		if (selectedAgent != null) {
			String history = agent.getConversationHistory(selectedAgent);
			textArea.setText(history);
		}
	}

	/**
	 * Actualizeaza istoricul conversatiei afisat in fereastra de chat.
	 * 
	 * @param history istoricul conversatiei care va fi afsat.
	 */
	public void updateConversationHistory(String history) {
		textArea.setText(history);
	}

	/**
	 * Populeaza lista de agenti cu agentii disponibili pentru conversatie.
	 * 
	 * @param agents lista agentilor disponibili.
	 */
	public void populateAgentList(List<AID> agents) {
		updateAgentList(agents);
	}
}
