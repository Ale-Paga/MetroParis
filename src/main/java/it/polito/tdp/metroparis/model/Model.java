package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	Graph<Fermata, DefaultEdge> grafo;
	Map<Fermata, Fermata> predecessore;
	
	public void creaGrafo() {
		this.grafo = new SimpleGraph<>(DefaultEdge.class);
		
		MetroDAO dao = new MetroDAO();
		List<Fermata> fermate = dao.getAllFermate();  //<fermata arrivo, fermata da dove arrivo>
		
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
		this.predecessore= new HashMap<>();
		this.predecessore.put(partenza, null);
		
		bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>() {   //ci serve a registrare gli step di iterazione e quindi risalire l'albero di visita
			
			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {  //si attiva quando attraverso vertice
				//System.out.println(e.getVertex());
			//	Fermata nuova = e.getVertex();
			//	Fermata precedente = null ; //vertice adiacente a 'nuova' che sia già raggiunto (cioè è già presente nelle key della mappa)
			//	predecessore.put(nuova, precedente);
			}
			
			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {				
			}
			
			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {			//si attiva quando attraverso arco (utile perché ci da pure i due vertici)	
				DefaultEdge arco = e.getEdge();
				Fermata a = grafo.getEdgeSource(arco); //vertice di partenza 
				Fermata b = grafo.getEdgeTarget(arco); //vertice di arrivo 
				//ho scoperto 'a' arrivando da 'b' (se 'b' lo conoscevo già e quindi è una delle key della map)
				if(predecessore.containsKey(b) && !predecessore.containsKey(a)) {
					predecessore.put(a, b);
					//System.out.println(a+" scoperto da "+b);
				}else if(predecessore.containsKey(a) && !predecessore.containsKey(b)){
					//di sicuro conoscevo 'a' e quindi ho scoperto 'b'
					predecessore.put(b, a);
					//System.out.println(b+" scoperto da "+a);
				}
			}
			
			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {				
			}
			
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {				
			}
		});
		
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
	
	
	//ora possiamo costruire i cammini
	public List<Fermata> trovaCammino(Fermata partenza, Fermata arrivo) {
		this.fermateRaggiungibili(partenza);
		//List<Fermata> result = new ArrayList<>();
		List<Fermata> result = new LinkedList<>(); //dovendo ogno volta aggiungere in testa meglio usare la linked perchè l'array sposta ogni volta tutti gli oggetti
		result.add(arrivo);
		Fermata f = arrivo;
		while(predecessore.get(f)!=null) {
			f=predecessore.get(f);
			result.add(0,f);
			
		}
		return result;
	}

}
