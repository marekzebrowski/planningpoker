package code.snippet
import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import code.comet.PokerServer
import code.model._
import util.Helpers._
import scala.xml.NodeSeq
import net.liftweb.common.Logger


object UserName {
  object userName extends SessionVar[String]("");
}

class PokerControl extends Logger {
  def saveUser(s:String) = {
    UserName.userName.set(s)
    PokerServer ! AddUser(s)
    
  }
  def user = ".login_user_name" #> UserName.userName.get
  
  def vote(v:String):Unit = {
   info("val rec "+v)
   val vv = if(v equals("?")) -1 else v.toInt 
   PokerServer ! Vote(UserName.userName.get,vv) 
  }
  
  def render = { 
	  if(UserName.userName.get.isEmpty()) {
		  ".with-name-only" #> "" &
		  "name=username" #> SHtml.onSubmit( s => {saveUser(s); RedirectTo("vote.html")} ) 
	  } else {
	      ".username" #> "" &
	      "name=show [onclick]" #> SHtml.ajaxInvoke(() =>  (PokerServer ! ShowAll) ) &
	      "name=new_vote  [onclick]" #> SHtml.ajaxInvoke( () => (PokerServer ! NewVoting )) &
	      ".actbuttons input [onclick]" #> SHtml.ajaxCall( JsRaw("$(this).val()") , vote )
	  }
  }

}