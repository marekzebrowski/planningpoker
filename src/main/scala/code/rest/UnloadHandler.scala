package code.rest

import net.liftweb._
import common._
import http._
import rest._
import util._
import Helpers._
import json._
import code.snippet.UserStore

object UnloadHandler extends RestHelper {
	serve {
	  case "v1" :: "onbeforeunload" :: Nil Get _ =>  
	    UserStore.leave 
	    new AcceptedResponse
	} 
}