package code.comet
import net.liftweb._
import http._
import util._
import Helpers._



class Poker  extends CometActor with CometListener {
	def registerWith = PokerServer
	
	var players:Map[String,Option[Int]] = Map()
	var ready = false 
	/**
	 * game state can be ready or not
	 * any player can be waiting or ready - that is represented by option state
	 * 
	 */
	def calcState() = {
	  //if there is no None in map game is ready
	  ready =  ! players.values.exists( _==None)  
	}
	
	override def lowPriority = {
		case v: Map[String,Option[Int]] => players = v; calcState(); reRender()
	}
	
	def isReady(x :Option[Int]):String = x match {
	  case Some(_) => "+"
	  case None => "?"
	}
	 
	/**
	 * vote state - number - ready and voted, + - read, other waiting, ? - not voted yet
	 */
	def vote(x :Option[Int]):String = x match {
	  case Some(v) => if(ready) v.toString else "+" 
	  case None => "?"
	}
	
	def render = {
		".uservote *" #> players.map(p => ".name" #> p._1 & ".vote *" #> vote(p._2) ) 
	}
	
}