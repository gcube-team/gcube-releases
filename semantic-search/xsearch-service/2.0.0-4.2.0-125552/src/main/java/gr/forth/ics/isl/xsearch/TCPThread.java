package gr.forth.ics.isl.xsearch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class TCPThread extends Thread {

    Socket socket;
    ObjectInputStream Sinput;
    ObjectOutputStream Soutput;

    TCPThread(Socket socket) {
        super();
        this.socket = socket;
    }

    @Override
    public void run() {
        /* Creating both Data Stream */
        System.out.println("Thread trying to create Object Input/Output Streams");
        try {
            // create output first
            Soutput = new ObjectOutputStream(socket.getOutputStream());
            Soutput.flush();
            Sinput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Exception creating new Input/output Streams: " + e);
            return;
        }
        System.out.println("Thread waiting for a String from the Client");
        // read a String (which is an object)
        try {
            
            Map<String,String> map = (Map<String,String>) Sinput.readObject();
            
            
            Bean_Search results;
            try {
                results = new Bean_Search(map.get("query"), 
                                          Integer.parseInt(map.get("resultsStartOffset")),
                                          Boolean.parseBoolean(map.get("clustering")),
                                          Integer.parseInt(map.get("clnum")),  
                                          Boolean.parseBoolean(map.get("mining")), 
                                          Boolean.parseBoolean(map.get("OnlySnippets")),
                                          map.get("locator"));
                
                String result = results.getJsonResults();
                Soutput.writeObject(result);
                Soutput.flush();
            } catch (Exception ex) {
                Logger.getLogger(TCPThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException e) {
            System.out.println("Exception reading/writing  Streams: " + e);
            return;
        } // will surely not happen with a String
        catch (ClassNotFoundException o) {
        } finally {
            try {
                Soutput.close();
                Sinput.close();
            } catch (Exception e) {
            }
        }
    }
}
