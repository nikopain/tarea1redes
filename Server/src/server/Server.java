package server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author niko
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    private static int port= 4000;
    public static void main(String[] args) throws IOException {
        try{
            ServerSocket skts = new ServerSocket(port);
            while (true){
                Socket cl= skts.accept();
                Clientes Tcl= new Clientes(cl);
                Tcl.start();
            }
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

    private static class Clientes extends Thread{
        private Socket cliente=null;
        private PrintWriter os=null;
        private String nombre;
        private String dirIp;
        private String puerto;
        public Clientes(Socket cl) {
            cliente=cl;
        }
        public void run(){
            try{
                BufferedReader in=new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                //el true es para autoflush, "8859_1" es un charset
                os = new PrintWriter(new OutputStreamWriter(cliente.getOutputStream(),"8859_1"),true);
                //System.out.println(currentThread().toString() + " - " + in.readLine());
                String ruta= "";//Se guarda la cadena de la peticion para luego procesarla y obtener la url
                int i =0;
                String next;
                ruta=in.readLine();
                do			
                {                    
                    //ruta = in.readLine();
                    if(i == 0) // la primera linea nos dice que fichero hay que descargar
                    {
                        i++;
                        StringTokenizer st = new StringTokenizer(ruta);
                        next= st.nextToken();
                       // System.out.println(st.countTokens());
                        if ((st.countTokens() >= 2) && next.equals("GET")) 
                        {
                            //System.out.println(next);
                            //next=;
                            retornaFichero(st.nextToken()) ;
                            /*System.out.println(next);
                            i=0;*/
                        }
                        else if((st.countTokens()>=2)&&next.equals("POST")){
                            //database.setWritable(true);
                            //System.out.println(next);
                            //imprimirFichero(st.nextToken());
                            String currentLine =null;
                            do{
                                currentLine = in.readLine();
                                //System.out.println(currentLine);                                   
                                if((currentLine.indexOf("Content-Disposition:")) != -1){
                                    if(currentLine.indexOf("nombre")!= -1){//REVISA SI TIENE EL NAME=NOMBRE PUESTO EN EL FORM DEL HTML
                                        currentLine= in.readLine();//PARA SALTAR INFO INNECESARIA
                                        currentLine= in.readLine();//PARA SALTAR INFO INNECESARIA
                                        nombre=currentLine;
                                        //System.out.println("nombre guardado");
                                    }
                                    else if(currentLine.indexOf("dirip")!= -1){//REVISA SI TIENE EL NAME=DIRIP PUESTO EN EL FORM DEL HTML
                                        currentLine= in.readLine();//PARA SALTAR INFO INNECESARIA
                                        currentLine= in.readLine();//PARA SALTAR INFO INNECESARIA
                                        dirIp=currentLine;
                                        //System.out.println("ip guardado");
                                    }
                                    else if(currentLine.indexOf("puerto")!= -1){//REVISA SI TIENE EL NAME=PUERTO PUESTO EN EL FORM DEL HTML
                                        currentLine= in.readLine();//PARA SALTAR INFO INNECESARIA
                                        currentLine= in.readLine();//PARA SALTAR INFO INNECESARIA
                                        puerto=currentLine;
                                        //System.out.println("puerto guardado");
                                    }
                                }
                            }while(in.ready());
                            //System.out.println(nombre);
                            //System.out.println(dirIp);
                            //System.out.println(puerto);
                            next= st.nextToken();
                            if(next.equals("/pag1.html")||next.equals("/pag2.html")){
                                retornaFichero(next);
                            }
                            else if(next.equals("/menu.html")){
                                imprimirFichero(next,nombre,dirIp,puerto);
                            }
                           // System.out.println(next);
                            //retornaFichero(st.nextToken());
                            //i=0;
                        }
                        else 
                        {
                            os.println("400 Petición Incorrecta") ;
                        }
                    }
                    ruta=in.readLine();

                }while (ruta != null && ruta.length() != 0);
                
            }catch(Exception e){
                System.out.println( e);
            }
            
        }
        void retornaFichero(String sfichero)
	{
            // comprobamos si tiene una barra al principio
            String opciones;
            if (sfichero.startsWith("/"))
            {
                    sfichero = sfichero.substring(1) ;
            }
            // si acaba en /, le retornamos el index.htm de ese directorio
            // si la cadena esta vacia, no retorna el index.htm principal
            if (sfichero.endsWith("/") || sfichero.equals(""))
            {
                    sfichero = sfichero + "menu.html" ;
            }
            try
            {
                // Ahora leemos el fichero y lo retornamos
                if(sfichero.equals("pag2.html")){
                    String contactos="";
                    File cont= new File("contactos.txt");
                    if(cont.exists()){
                        StringTokenizer s;
                        BufferedReader fLocal= new BufferedReader(new FileReader(cont));
                        String lin="";
                        String comienzo="<html>\n" +
"    <head>\n" +
"        <title></title>\n" +
"        <meta charset=\"UTF-8\">\n" +
"        <meta name=\"viewport\" content=\"width=device-width\">\n" +
"    </head>\n" +
"    <body>\n" +
"        <div>\n"+
"           <select name=\"contactos\" multiple=\"multiple\">";
                       while((lin=fLocal.readLine())!=null){
                            s= new StringTokenizer(lin);
                            contactos= contactos+"<option>"+ s.nextToken()+"</option>\n";
                            
                        }
                        String fin= "</select>\n" +
"        <textarea rows=\"5\" cols=\"50\"></textarea>    \n" +
"        </div>\n" +
"        <form method=\"POST\" action=\"pag1.html\">\n" +
"            <input type=\"submit\" value=\"Agregar Contacto\">\n" +
"        </form>\n" +
"    </body>\n" +
"</html>";
                        os.println(comienzo+contactos+fin);
                        fLocal.close();
                        os.close();
                    }
                    else{
                        os.println("HTTP/1.0 400 ok");
                        os.close();
                    }
                    //System.out.println(contactos);
                }
                else{
                File mifichero = new File(sfichero) ;
                
                if (mifichero.exists()) 
                {

                    BufferedReader ficheroLocal = new BufferedReader(new FileReader(mifichero));
                    String linea = "";
                    do			
                    {
                        linea = ficheroLocal.readLine();
                       // if(linea.)
                        if (linea != null )
                        {
                            // sleep(500);
                            os.println(linea);
                        }
                    }
                    while (linea != null);

                    ficheroLocal.close();
                    os.close();

                }  // fin de si el fiechero existe 
                else
                {	
                    os.println("HTTP/1.0 400 ok");
                    os.close();
                }
                }

            }
            catch(Exception e){
            }

        }

        private void imprimirFichero(String nextToken,String nombre1,String dirIp1,String puerto1) {
            Writer writer = null;

            try {
                
                writer = new BufferedWriter(new OutputStreamWriter(
                      new FileOutputStream("contactos.txt",true), "utf-8"));
                        writer.write("\r\n");
                        writer.write(nombre1+ " ");
                        writer.write(dirIp1+ " ");
                        writer.write(puerto1);
            } catch (IOException ex) {
              // report
            } finally {
               try {writer.close();} catch (Exception ex) {}
            }
            retornaFichero(nextToken);
            //lee archivo si no existe
            
        }
    }
    
}
