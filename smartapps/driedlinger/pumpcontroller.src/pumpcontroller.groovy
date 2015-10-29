/**
 *  pumpController
 *
 *  Copyright 2015 Darin Riedlinger
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "pumpController",
    namespace: "driedlinger",
    author: "Darin Riedlinger",
    description: "Pump cycle controller - run pump x amount of time for x number of iterations. ",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	//section("When I touch the app, start clcle on...") {
	//	input "theswitch", "capability.switch", multiple: true
	//}
    section("Turn on the pump") {
        input "theswitch", "capability.switch", required: true
    	}
    section("Sleep between pump cycles X minutes") {
            input "sleepTime", "number", required: true, title: "Minutes?"
    }
    section("Run pump for X minutes") {
            input "runTime", "number", required: true, title: "Minutes?"
    }
    section("Run the pump X cycles") {
            input "cycles", "number", required: true, title: "Cycles?"
    }
}


def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribe(location, changedLocationMode)
	subscribe(app, appTouch)
    // initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
    subscribe(location, changedLocationMode)
    def cycleCnt = 0
	//subscribe(app, appTouch)
	initialize()
}

def initialize() {
//TODO: subscribe to attributes, devices, locations, etc.
//  subscribe(theswitch, "switch.status", pumpControl)
   def cycleCnt = 0
   log.debug "Set cycleCnt to: $cycleCnt"
	pumpControl(cycleCnt)
}

def pumpControl(cycleCnt){
   	log.debug "pumpContol: called"
	log.debug "pumpContol: cycleCnt is $cycleCnt of $cycles Cycles!"
    if (cycleCnt < cycles){
        cycleCnt ++ 
        log.debug "Incremented cycleCnt to $cycleCnt"
   		startPumpHandler(cycleCnt)
    }
    else{
     	log.debug "pumpContol: Is DONE!"
     }
}

def appTouch(evt) {
	log.debug "appTouch: $evt"
    def cycleCnt = 0
    log.debug "Set cycleCnt to: $cycleCnt"
    startPumpHandler(cycleCnt)
}

def stopPumpHandler(cycleCnt) {
    log.debug "stopPumpHandler called"
    theswitch.off()	
    log.debug "turned off pump"
  //  try {
    //  runIn(1000 * sleepTime, pumpControl(cycleCnt))
  //    }
  //  catch (e)
//	{
//	log.debug "STOP exception e caught: $e"
  //  }
}   
    
def startPumpHandler(cycleCnt) {
    log.debug "startPumpHandler called: cycleCnt: $cycleCnt"
    cycleCnt ++
    theswitch.on()
    log.debug "turned on pump"
    try {
    	def runt = 60 * runTime
        runIn(runt, stopPumpHandler(cycleCnt))
    }
    catch(e)
    {
        log.debug "START exception e caught: $e"
    }
   
}  