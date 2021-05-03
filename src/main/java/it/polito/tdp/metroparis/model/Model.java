package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	Graph<Fermata, DefaultEdge> grafo;
	
	public void creaGrafo() {
		this.grafo = new SimpleGraph<>(DefaultEdge.class);
		
		MetroDAO dao = new MetroDAO();
		List<Fermata> fermate = dao.getAllFermate();
		
		//for(Fermata f: fermate) {
			//this.grafo.addVertex(f);
		//}
		
		Graphs.addAllVertices(this.grafo, fermate);  //fa la stessa cosa del for
		
		//System.out.println(this.grafo);
		
		//Aggiungiamo gli archi
		/*for(Fermata f1: this.grafo.vertexSet()) {
			for(Fermata f2: this.grafo.vertexSet()) {
				if(!f1.equals(f2) && dao.fermateCollegate(f1,f2)) {
					this.grafo.addEdge(f1, f2);
				}
			}
		}*/
		
		List<Connessione> connessioni = dao.getAllConnessioni(fermate);
		for(Connessione c: connessioni) {
			this.grafo.addEdge(c.getStazA(), c.getStazP());
		}
		
		System.out.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(), this.grafo.edgeSet().size()) ;
		
		//System.out.println(this.grafo);
		
		
		//COME FARE PER TROVARE I VERTICI VICINI AD UN DETERMINATO VERTICE?
//		Fermata f=null;
		/*
		Set<DefaultEdge> archi = this.grafo.edgesOf(f);
		for(DefaultEdge e: archi) {
			/*Fermata f1 = this.grafo.getEdgeSource(e);
			//oppure
			Fermata f2 = this.grafo.getEdgeTarget(e);
			if(f1.equals(f)) {
				//f2 è quello che mi serve
			}else {
				//f1 è quello che miserve
			}
			
			//questo comando fa la stessa cosa
			
			Fermata f1 = Graphs.getOppositeVertex(this.grafo, e, f);
		} */
		
		//altro comando uguale
//		List<Fermata> fermateAdiacenti = Graphs.successorListOf(grafo, f);  //con questo non ci interessano più gli archi
		//E SE VOGLIO L'ELENCO DI VERTICI PRECEDENTI? CIOè ENTRANTI?
//		List<Fermata> fermateAdiacent = Graphs.predecessorListOf(grafo, f);
		//bel caso di grafo bidirezionale uso Graphs.outgoingEdgesOf()
		
		
		
	}
	
	//voglio trovare tutti vertci raggiungibili da un certo vertice
	//facciamo visita in ampiezza
	public List<Fermata> fermateRaggiungibili(Fermata partenza){
		BreadthFirstIterator<Fermata, DefaultEdge> bfv = new BreadthFirstIterator<>(this.grafo, partenza);
		List <Fermata> result = new ArrayList<>();
		
		while(bfv.hasNext()) {  //ogni volta che hasNext è vero vuol dire che ci sono altri vertici da scorprire
			Fermata f = bfv.next();
			result.add(f);
		}
		return result;
	}
	
	//facciamo visita in profondità
	public List<Fermata> fermateRaggiungibili2(Fermata partenza){
		DepthFirstIterator<Fermata, DefaultEdge> dfv = new DepthFirstIterator<>(this.grafo, partenza);
		List <Fermata> result = new ArrayList<>();
		
		while(dfv.hasNext()) {  //ogni volta che hasNext è vero vuol dire che ci sono altri vertici da scorprire
			Fermata f = dfv.next();
			result.add(f);
		}
		return result;
	}
	
	public Fermata trovaFermata(String nome) {
		for(Fermata f: this.grafo.vertexSet()) {
			if(f.getNome().equals(nome)) {
				return f;
			}
		}
		return null;
	}

}
