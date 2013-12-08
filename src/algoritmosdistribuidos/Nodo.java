/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmosdistribuidos;

import com.trendrr.beanstalk.BeanstalkClient;
import com.trendrr.beanstalk.BeanstalkException;
import com.trendrr.beanstalk.BeanstalkJob;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pablo
 */
public class Nodo extends Thread {

    private final String SIGNAL = "SIGNAL";
    private final String FIN = "FIN";
    private final String HOST = "localhost";
    private final int PORT = 11300;

    private int id, inDeficit, outDeficit;
    private int idPadre = -1;
    private int deudores[];
    private boolean terminado;
    private BeanstalkClient Client;
    private String tube;
    private List<Integer> inDeficits;
    private List<Integer> idPredecesores;
    private List<Integer> idSucesores;

    public Nodo(int id) {
        this.id = id;
        this.inDeficit = 0;
        this.outDeficit = 0;
        this.idPadre = -1;
        this.terminado = false;
        idPredecesores = new ArrayList<>();
        idSucesores = new ArrayList<>();
        inDeficits = new ArrayList<>();
        tube = String.valueOf(id);
        Client = new BeanstalkClient(HOST, PORT, tube);
    }

    public Nodo(int id, int idPadre) {
        this.id = id;
        this.inDeficit = 0;
        this.outDeficit = 0;
        this.idPadre = idPadre;
        this.terminado = false;
        idPredecesores = new ArrayList<>();
        idSucesores = new ArrayList<>();
        inDeficits = new ArrayList<>();
        tube = String.valueOf(id);
        Client = new BeanstalkClient(HOST, PORT, tube);
    }

    /**
     * Método para enviar un mensaje de un nodo a otro. Se envía un mensaje de
     * la cola de mensajes del nodo a otro nodo especi ficado por id. El
     * outDeficit del nodo emisor aumenta.
     *
     * @param idDestino
     */
    public void sendMensj(String mensaje, int idReceptor, int myId) {
        //enviamos el mensaje al nodo indicado
        if (idPadre != -1) {//solo nodos activos
            //send(mensaje,idReceptor,miId);
            outDeficit++;
        }

    }

    public void receiveMensj(String mensaje, int idEmisor) {
        if (idPadre == -1) {
            idPadre = idEmisor;
            //TODO: hacer algo más??
        }
        int index = idPredecesores.get(idEmisor);
        idPredecesores.set(index, inDeficits.get(index) + 1);
        inDeficit++;
    }

    public boolean sendSignal(/*signal, E, */int myId) {        
        if (inDeficit > 1) {
            int i;
            for (i=0; i<inDeficits.size();i++) {
                if((inDeficits.get(i)>1) || (inDeficits.get(i)==1 && idPredecesores.get(i)!=idPadre)) break;
            }
            
            if (i<inDeficits.size()){
                sendMensj(SIGNAL,idPredecesores.get(i), id);
                inDeficits.set(i, inDeficits.get(i)-1);
                inDeficit--;
                return true;
            }
            return false;
        } else if ((inDeficit == 1) && (terminado) && (outDeficit == 0)) {
            //send(signal, parent, myID)
            inDeficits.set(inDeficits.indexOf(idPadre),0);
            inDeficit=0;
            idPadre=-1;
            return true;
        }
        return false;
    }

    public void receiveSignal() {
        //receive(signal,_);
        outDeficit--;
    }

    public boolean Terminado(int inDeficit) {
        return terminado = (inDeficit == 0);
    }

    //devuelve el identificador del nodo
    public int getNodeId() {
        return this.id;
    }

    /*Añade un hijo al nodo*/
    public void addSucesor(int idSucesor) {
        idSucesores.add(idSucesor);
    }

    /*Añade un predecesor al nodo*/
    public void addPredecesor(int idPredecesor) {
        idPredecesores.add(idPredecesor);
    }

    public List<Integer> predecesores() {
        return idPredecesores;
    }

    public List<Integer> sucesores() {
        return idSucesores;
    }

    boolean hasSucesor(int id) {
        for (int sucesor : idSucesores) {
            if (sucesor == id) {
                return true;
            }
        }
        return false;
    }

    boolean hasPredecesor(int id) {
        for (int predecesor : idPredecesores) {
            if (predecesor == id) {
                return true;
            }
        }
        return false;
    }

    void print() {
        System.out.println("id:\t" + id);
        System.out.println("padre:\t" + idPadre);
        System.out.println("predecesores:\t" + idPredecesores.toString());
        System.out.println("sucesores:\t" + idSucesores.toString());
    }

    void initDeficits() {
        //ponemos a cero todos los inDeficit del nodo
        for (int i = 0; i < idPredecesores.size(); i++) {
            inDeficits.add(0);
        }
    }
}
