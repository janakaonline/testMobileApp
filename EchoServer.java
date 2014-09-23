
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer
{

    private static final int local_server_port = 9090;

    public static void main(String[] args) throws IOException
    {
        Server server = new Server(local_server_port);
        server.Init();
        server.Run();
    }
}

class Server extends Thread
{

    private final int local_server_port;
    private ServerSocket server_socket;

    public Server(int local_server_port)
    {
        this.local_server_port = local_server_port;
    }

    void Init() throws IOException
    {
        server_socket = new ServerSocket(local_server_port);
    }

    void Run() throws IOException
    {
        while (true)
        {
            ClientHandler client_handler = new ClientHandler(server_socket.accept());
            client_handler.Init();
            client_handler.start();
        }
    }
}

class ClientHandler extends Thread
{

    private final Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private InputStream inputStream;
    private OutputStream outputStream;

    private String ClientID;

    public ClientHandler(Socket socket)
    {
        this.socket = socket;
    }

    public void Init() throws IOException
    {
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        reader = new BufferedReader(new InputStreamReader(inputStream));
        writer = new PrintWriter(outputStream, true);
        ClientID = socket.getLocalAddress() + ":" + socket.getPort();
        System.out.println("new Client @ " + ClientID);
    }

    private void Run() throws IOException
    {
        while (inputStream.available() > 1)
        {
            String write_line = ClientID + ">> " + reader.readLine();
            writer.println(write_line);
            System.out.println(write_line);
        }
    }

    private void CloseConnection() throws IOException
    {
        writer.close();
        socket.close();
        System.out.println("Client disconnected..");
    }

    @Override
    public void run()
    {
        try
        {
            Init();
            while (true)
            {
                Run();
            }
            //CloseConnection();
        } catch (IOException ex)
        {
            System.out.println("Error @ Client handler: " + ex.getMessage());
        }
    }
}
