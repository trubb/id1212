package peer.controller;

import common.PeerList;
import peer.net.server.PeerInfo;

/**
 * Deals with checking if all peers in the net have let us know that they are done
 * And calculates the score when all peers have made their moves, thereby ending the round
 */
public class GameManager {

    /**
     * Check if all peers in the PeerList have registered moves
     * @param peerList         the PeerList we check in
     * @param currentPeerInfo   this client's info
     * @return                  the round's score
     */
    public static String checkEndGame ( PeerList peerList, PeerInfo currentPeerInfo ) {
        String printout = "";

        // if all peers have made their moves and we have made a move ourselves
        if ( peerList.allPeersPlayed() && currentPeerInfo.getCurrentMove() != null ) {
            calculateScore(peerList, currentPeerInfo);    // calculate the round score
            // put the relevant score into the printout string
            printout = "SCORE OF THIS ROUND: " + currentPeerInfo.getRoundScore() +
                    " - OVERALL SCORE: " + currentPeerInfo.getTotalScore();
            peerList.resetPeerMoves();    // reset the moves of all peers in the net
            currentPeerInfo.setCurrentMove( null ); // reset the move of this client
            currentPeerInfo.resetRoundScore();  // reset the roundscore tally
        }
        return printout;
    }

    /**
     * Calculate the score of the current game
     * @param peerList         the list of peers
     * @param currentPeerInfo   this client's info
     */
    private static void calculateScore ( PeerList peerList, PeerInfo currentPeerInfo ) {

        for ( PeerInfo peer : peerList.getPeerInfo() ) {  // for every peer in the peertable
            String move = peer.getCurrentMove();  // grab their move

            for ( PeerInfo otherPeer : peerList.getPeerInfo() ) {    // compare peer1's move to every other peer's moves
                if ( !otherPeer.getID().equals( peer.getID() ) ) { // as long as peer1 != peer2
                    String otherPeerCurrentMove = otherPeer.getCurrentMove();  // grab the other peer's move

                    // if the first client chose PAPER, they win over the other peer if they chose ROCK
                    if( move.equals("PAPER") ) {
                        if ( otherPeerCurrentMove.equals( "ROCK" ) ) {
                            peer.setRoundScore( 1 );
                        } else {
                            peer.setRoundScore( 0 );
                        }
                    }
                    // if the first client chose ROCK, they win over the other peer if they chose SCISSORS
                    if( move.equals("ROCK") ) {
                        if ( otherPeerCurrentMove.equals( "SCISSORS" ) ) {
                            peer.setRoundScore( 1 );
                        } else {
                            peer.setRoundScore( 0 );
                        }
                    }
                    // if the first client chose SCISSORS, they win over the other peer if they chose PAPER
                    if( move.equals("SCISSORS") ) {
                        if ( otherPeerCurrentMove.equals( "PAPER" ) ) {
                            peer.setRoundScore( 1 );
                        } else {
                            peer.setRoundScore( 0 );
                        }
                    }
                }
            }
            peer.setTotalScore( peer.getRoundScore() ); // add the score to the selected peer's score
            peer.resetRoundScore(); // reset their score for this round
        }

        String move = currentPeerInfo.getCurrentMove(); // grab this client's current move

        for ( PeerInfo otherPeer : peerList.getPeerInfo() ) {  // compare our move to every peer's move
            String otherPeerMove = otherPeer.getCurrentMove();  // grab the peer's move

            // if we chose PAPER, we win over the other peer if they chose ROCK
            if ( move.equals("PAPER") ) {
                if ( otherPeerMove.equals( "ROCK" ) ) {
                    currentPeerInfo.setRoundScore( 1 );
                } else {
                    currentPeerInfo.setRoundScore( 0 );
                }
            }
            // if we chose ROCK, we win over the other peer if they chose SCISSORS
            if ( move.equals("ROCK") ) {
                if ( otherPeerMove.equals( "SCISSORS" ) ) {
                    currentPeerInfo.setRoundScore( 1 );
                } else {
                    currentPeerInfo.setRoundScore( 0 );
                }
            }
            // if we chose SCISSORS, we win over the other peer if they chose PAPER
            if ( move.equals("SCISSORS") ) {
                if ( otherPeerMove.equals( "PAPER" ) ) {
                    currentPeerInfo.setRoundScore( 1 );
                } else {
                    currentPeerInfo.setRoundScore( 0 );
                }
            }
        }
        currentPeerInfo.setTotalScore( currentPeerInfo.getRoundScore() );   // add our score from this round to the total
    }

}
