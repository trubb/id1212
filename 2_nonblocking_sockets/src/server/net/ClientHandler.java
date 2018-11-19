package server.net;

import server.model.HangManGame;
import shared.Message;
import shared.MessageType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

import static shared.Message.deserialize;
import static shared.Message.serialize;
import static shared.MessageType.GUESS;
import static shared.MessageType.QUIT;
import static shared.MessageType.START;

public class ClientHandler implements Runnable {
    private final Server server;
    private final SocketChannel clientChannel;
    private final ByteBuffer clientMessage = ByteBuffer.allocate(8192); // TODO - first check for nonworking shit
    private final LinkedBlockingQueue<Message> readingQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<>();
    private SelectionKey selectionKey;  // key that handler is registered for
    private HangManGame hangManGame = new HangManGame();


    ClientHandler ( Server server, SocketChannel clientChannel ) {
        this.server = server;
        this.clientChannel = clientChannel;
    }

    @Override
    public void run() {
        Iterator<Message> iterator = readingQueue.iterator();

        while (iterator.hasNext()) {
            Message message = iterator.next();

            if (message.getMessageType() == START) {
                String currentState = this.hangManGame.startRound();
                sendResponseToClient(MessageType.START_RESPONSE, currentState);
            } else if (message.getMessageType() == GUESS) {
                String currentState1 = this.hangManGame.validateGuess(message.getMessage());
                if (this.hangManGame.getChosenWord() == null) {
                    sendResponseToClient(MessageType.END_RESPONSE, currentState1);
                } else {
                    sendResponseToClient(MessageType.GUESS_RESPONSE, currentState1);
                }
            } else if (message.getMessageType() == QUIT) {
                disconnectClient();
            } else {
                System.out.println("Messagetype not recognized");
            }
            iterator.remove();
        }
    }

    void readMessage() throws IOException {
        clientMessage.clear();
        int numOfReadBytes = clientChannel.read(clientMessage);
        if (numOfReadBytes == -1) {
            throw new IOException("Client closed connection");
        }
        readingQueue.add( deserialize( extractMessageFromBuffer() ) );
        ForkJoinPool.commonPool().execute(this);
    }

    void writeMessage() throws IOException {
        synchronized (sendingQueue) {
            while (sendingQueue.size() > 0) {
                ByteBuffer message = ByteBuffer.wrap( serialize (sendingQueue.poll() ).getBytes() );    // TODO - another fault source
                clientChannel.write(message);
            }
        }
    }

    void disconnectClient() {
        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setSelectionKey (SelectionKey key) {
        this.selectionKey = selectionKey;
    }

    private String extractMessageFromBuffer() {
        clientMessage.flip();
        byte[] bytes = new byte[clientMessage.remaining()];
        clientMessage.get(bytes);
        return new String(bytes);
    }

    private void sendResponseToClient (MessageType messageType, String response) {
        Message message = new Message(messageType, response);

        synchronized (sendingQueue) {
            sendingQueue.add(message);
        }

        server.addMessageToWritingQueue(this.selectionKey);
        server.wakeupSelector();
    }

}
