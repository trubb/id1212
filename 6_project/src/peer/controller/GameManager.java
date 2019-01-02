package peer.controller;

import common.PeerTable;
import peer.net.server.PeerInfo;

public class GameManager {

    public static String checkEndGame(PeerTable peerTable, PeerInfo currentPeerInfo) {
        String printout = "";

        if ( peerTable.allPeersPlayed() && currentPeerInfo.getCurrentMove() != null ) {
            calculateScore( peerTable, currentPeerInfo);
            printout = "SCORE OF THIS ROUND: " + currentPeerInfo.getRoundScore() +
                    " - OVERALL SCORE: " + currentPeerInfo.getTotalScore();
            peerTable.resetPeersMoves();
            currentPeerInfo.setCurrentMove( null );
            currentPeerInfo.resetRoundScore();
        }
        return printout;
    }

    private static void calculateScore (PeerTable peerTable, PeerInfo currentPeerInfo) {
        for ( PeerInfo peer1 : peerTable.getPeerInfo() ) {
            String move1 = peer1.getCurrentMove();

            for (PeerInfo peer2 : peerTable.getPeerInfo() ) {
                if ( !peer2.getID().equals( peer1.getID() ) ) {
                    String move2 = peer2.getCurrentMove();

                    if ( move1.equals("PAPER") ) {
                        peer1.setRoundScore( move2.equals("ROCK") ? 1 : 0 );    // TODO BREAK THIS SHIT OUT OF POOFORM
                    }
                    if ( move1.equals("ROCK") ) {
                        peer1.setRoundScore( move2.equals("SCISSORS") ? 1 : 0 );    // TODO BREAK THIS SHIT OUT OF POOFORM
                    }
                    if ( move1.equals("SCISSORS") ) {
                        peer1.setRoundScore( move2.equals("PAPER") ? 1 : 0 );    // TODO BREAK THIS SHIT OUT OF POOFORM
                    }
                }
            }

            peer1.setTotalScore( peer1.getRoundScore() );
            peer1.resetRoundScore();
        }

        String move1 = currentPeerInfo.getCurrentMove();

        // tODO - felkälla nedan
        for ( PeerInfo peer2 : peerTable.getPeerInfo() ) {
            String move2 = peer2.getCurrentMove();

            if ( move1.equals("PAPER") ) {
                currentPeerInfo.setRoundScore( move2.equals("ROCK") ? 1 : 0 );
            }
            if ( move1.equals("ROCK") ) {
                currentPeerInfo.setRoundScore( move2.equals("SCISSORS") ? 1 : 0 );
            }
            if ( move1.equals("SCISSORS") ) {
                currentPeerInfo.setRoundScore( move2.equals("PAPER") ? 1 : 0 );
            }
        }

        currentPeerInfo.setTotalScore( currentPeerInfo.getRoundScore() );

    }

}
