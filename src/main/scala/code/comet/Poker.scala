package code.comet
import net.liftweb._
import http._
import util._
import Helpers._
import net.liftweb.http.js.JE.JsFunc
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JE.Call

class Poker  extends CometActor with CometListener {
	def registerWith = PokerServer
	
	var state: Voting = VotingProgress(Map())

	override def lowPriority = {
		case v: Voting => state = v;  reRender()
	}
	
	def isReady(x :Option[Int]):String = x match {
	  case Some(_) => "✓"
	  case None => "…"
	}

		
	def toPresentation(x :Option[Int]):String = x match {
	  case Some(v:Int) => if(v<0) "?" else if(v>100) "∞" else v.toString
	  case None => "…"
	}

	def finalResultPres(v:Int):String = if(v>0) v.toString else "--"
	
	def render = {
	  state match {
	    case pr:VotingProgress => 
	      ".uservote *" #> pr.votes.map( p => ".name"   #> p._1 & 
	          							      ".vote *" #> isReady(p._2) ) &
	      ".finalresults" #> ""          							      
	    case vc:VotingComplete => 
	      val cssTr = 
	      ".uservotes" #> {
	    	  ".uservote" #> vc.votes.map( p =>  ".name" #> p._1 & 
	          								  ".vote *" #> toPresentation(p._2) &
	          								  ".uservote [class+]" #> vc.isExtreme(p._2) 
	          								  ) 
	      } &
	      ".finalresults" #> finalResultPres(vc.finalResult)
	      val nodes = cssTr(defaultHtml) 
	      new RenderOut(nodes,Call("animateResults").cmd)
	  }
	}
	
}