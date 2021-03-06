/**
 *  Controller
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
    name: "Controller",
    namespace: "driedlinger",
    author: "Darin Riedlinger",
    description: "Controls a switch.",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	//section("When I touch the app, start cycle on...") {
	//	input "theswitch", "capability.switch", multiple: true
	//}
    section("Turn on the pump") {
        input "theswitch", "capability.switch", required: true
    	}
    section("Sleep between pump cycles X minutes") {
            input "sleepTime", "number", required: true, title: "Interval Hours?"
    }
    section("Run pump for X minutes") {
            input "runTime", "number", required: true, title: "Run Time Minutes?"
    }
    section("Run the pump X cycles") {
            input "cycles", "number", required: true, title: "Number of Cycles?"
    }
}


def installed() {
	log.debug "Installed with settings: ${settings}"
	//subscribe(location, changedLocationMode)
	subscribe(app, appTouch)
    atomicState.cycleCnt = 0 
    initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
    subscribe(app, appTouch)
    //subscribe(location, changedLocationMode)
    // def cycleCnt = 0
    atomicState.cycleCnt = 0 
	//subscribe(app, appTouch)
	initialize()
}

def initialize() {
//TODO: subscribe to attributes, devices, locations, etc.
//  subscribe(theswitch, "switch.status", pumpControl)
   subscribe(app, appTouch)
   def cycleCnt = 0
   atomicState.cycleCnt = 0
   log.debug "Initialized cycleCnt to: $atomicState.cycleCnt"
   //pumpControl()
}

def appTouch(evt) {
	log.debug "appTouch: $evt"
    def cycleCnt = 0
    atomicState.cycleCnt = 0 
    log.debug "appTouch: Set cycleCnt to: $atomicState.cycleCnt"
    pumpControl()
}


def pumpControl() {
   	log.debug "pumpContol: Called"
	log.debug "pumpContol: Pump run cycle Count is: $atomicState.cycleCnt of $cycles cycles"
   if (atomicState.cycleCnt <= cycles){
        atomicState.cycleCnt = atomicState.cycleCnt +1 
        log.debug "pumpContol: Incremented cycleCnt to $atomicState.cycleCnt"
        log.debug "pumpContol: Calling startPumpHandler"
   		startPumpHandler()
     }
}
def stopPumpHandler() {
    log.debug "stopPumpHandler: Called"
    theswitch.off()	
    log.debug "stopPumpHandler: Turned off pump"
    //def now = new Date()
    //log.debug "time is $now"
    // convert hours to minutes then to seconds
    def sleept = sleepTime * 60 * 60
    log.debug "stopPumpHandler: Calling pumpControl again in: $sleepTime hours"
    log.debug "stopPumpHandler: Calling pumpControl again in: $sleept seconds"
    runIn(sleept, pumpControl)
    }   
    
def startPumpHandler() {
    log.debug "startPumpHandler: Called"
    log.debug "startPumpHandler: CycleCnt is at: $atomicState.cycleCnt"
    theswitch.on()
    log.debug "startPumpHandler: turned on pump"
    // convert runTime minutes into seconds
    def runt = runTime * 60
    log.debug "startPumpHandler: Calling stopPumpHandler in: $runTime minutes"
    runIn(runt, stopPumpHandler)
    }   