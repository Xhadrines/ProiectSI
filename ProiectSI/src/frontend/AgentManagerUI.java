package frontend;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import backend.AgentManager;
import jade.core.AID;

/**
 * AgentManagerUI reprezinta interfata utilizatorului pentru gestionarea
 * agentilor. Aceasta permite utilizatorului sa creeze agenti, sa vizualizeze
 * agentii existenti si sa opreasca agentii si containerul JADE.
 */
public class AgentManagerUI extends JFrame {

	private AgentManager agentManager;
	private JComboBox<AID> agentComboBox;
	private JTextField textField;
	private JButton createAgentButton;
	private JButton updateButton;
	private JButton shutdownButton;

	/**
	 * Constructorul clasei AgentManagerUI. Initializeaza interfata si actiunile
	 * asociate butoanelor.
	 * 
	 * @param agentManager Instanta agentului care administreaza agentii.
	 */
	public AgentManagerUI(AgentManager agentManager) {
		this.agentManager = agentManager;

		setTitle("Agent Manager");
		setSize(750, 150);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);

		getContentPane().setLayout(new GridLayout(3, 1));

		JPanel topPanel = new JPanel(new BorderLayout());
		agentComboBox = new JComboBox<>();
		updateAgentList(agentManager.getAllAgents());
		topPanel.add(agentComboBox, BorderLayout.WEST);

		updateButton = new JButton("Update");
		topPanel.add(updateButton, BorderLayout.EAST);

		JPanel middlePanel = new JPanel(new BorderLayout());
		textField = new JTextField();
		middlePanel.add(textField, BorderLayout.CENTER);

		createAgentButton = new JButton("Create Agent");
		middlePanel.add(createAgentButton, BorderLayout.EAST);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		shutdownButton = new JButton("Shutdown");
		bottomPanel.add(shutdownButton, BorderLayout.CENTER);

		getContentPane().add(topPanel);
		getContentPane().add(middlePanel);
		getContentPane().add(bottomPanel);

		// Actiune pentru butonul de actualizare
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateAgentList(agentManager.getAllAgents());
			}
		});

		// Actiune pentru campul de text (cand se apasa Enter)
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String agentName = textField.getText().trim();
				if (!agentName.isEmpty()) {
					agentManager.createAgent(agentName, "backend.ChatAgent");
					updateAgentList(agentManager.getAllAgents());
					textField.setText("");
				}
			}
		});

		// Actiune pentru butonul de creare a unui agent
		createAgentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String agentName = textField.getText().trim();
				if (!agentName.isEmpty()) {
					agentManager.createAgent(agentName, "backend.ChatAgent");
					updateAgentList(agentManager.getAllAgents());
					textField.setText("");
				}
			}
		});

		// Actiune pentru butonul de oprire a agentilor
		shutdownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				agentManager.shutdownAgents();
			}
		});
	}

	/**
	 * Actualizeaza lista de agenti afisata in combo box.
	 * 
	 * @param agents lista agentilor care vor fi adaugati in combo box.
	 */
	private void updateAgentList(List<AID> agents) {
		agentComboBox.removeAllItems();
		for (AID agent : agents) {
			agentComboBox.addItem(agent);
		}
	}
}
