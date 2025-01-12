package backend;

import java.util.ArrayList;
import java.util.List;

import frontend.AgentManagerUI;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;

/**
 * AgentManager este un agent care gestioneaza crearea si oprirea altor agenti,
 * precum si obtinerea informatiilor despre agentii inregistrati in sistemul
 * JADE. Acesta ofera o interfata pentru administrarea agentilor si controlul
 * containerului JADE.
 */
public class AgentManager extends Agent {

	private AgentManagerUI agentManagerUI;

	/**
	 * Initializeaza interfata utilizatorului si afiseaza un mesaj in consola cand
	 * agentul este pornit.
	 */
	@Override
	protected void setup() {
		agentManagerUI = new AgentManagerUI(this);
		agentManagerUI.setVisible(true);

		System.out.println(getLocalName() + " has started.");
	}

	/**
	 * Obtine lista tuturor agentilor inregistrati in sistemul JADE.
	 * 
	 * @return o lista de agenti (AID-uri) inregistrati.
	 */
	public List<AID> getAllAgents() {
		List<AID> agents = new ArrayList<>();
		try {
			DFAgentDescription template = new DFAgentDescription();
			DFAgentDescription[] result = DFService.search(this, template);
			for (DFAgentDescription dfAgentDescription : result) {
				agents.add(dfAgentDescription.getName());
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		return agents;
	}

	/**
	 * Creeaza un nou agent si il adauga in containerul JADE.
	 * 
	 * @param agentName  numele noului agent.
	 * @param agentClass clasa agentului care va fi creat.
	 */
	public void createAgent(String agentName, String agentClass) {
		try {
			AgentContainer container = getContainerController();
			container.createNewAgent(agentName, agentClass, null).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opreste toti agentii si inchide containerul JADE. Dupa oprirea agentilor,
	 * AgentManager este de asemenea oprit.
	 */
	public void shutdownAgents() {
		try {
			AgentContainer container = getContainerController();
			container.kill();
			System.out.println("All agents have been stopped and the JADE container has been shutdown.");

			doDelete();
			System.out.println("AgentManager has been stopped.");
		} catch (StaleProxyException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
