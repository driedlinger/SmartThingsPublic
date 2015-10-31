/**
 *  dailyPumpControl
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
    name: "dailyPumpControl",
    namespace: "driedlinger",
    author: "Darin Riedlinger",
    description: "Run pump control module on a daily schedule",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	    section("Choose Module for Pump") {
        input "theswitch", "capability.switch", required: true
    	}
    section("Select Days to Run") {
		input "days", "enum", title: "Set for specific day(s) of the week", multiple: true, required: false,
                options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
	}
    section ("Pump Cycle Start Time") {
            input "timeOfDay", "time", title: "Time to start pump?", required: true
            }
    section ("Pump Run Time Duration") {
            input "howLong", "number", title: "How long to run pump?", required: true
            }       
    section("Send Notifications?") {
            // input("recipients", "contact", title: "Send notifications to") 
             input "phone", "phone", title: "Send an SMS to this number?", required:false
             
         }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
   log.debug "Initialize: selected day(s) is: $days"
   if (getDaysOk(days)){
     def timenow = timeToday(timeOfDay, location.timeZone)
     log.info "Initialize: Looks like we are on a correct day"
     log.info "Initialize: job Scheduled at $timenow"
     schedule(timeToday(timeOfDay, location.timeZone), "startPumpHandler")
   }
   else
   {
     log.info "We are not on a selected day"
   }
   
}


def getDaysOk(days) {
    def result = false
    log.info "getDays: Past in day is: $days"
    if (days) {
        def df = new java.text.SimpleDateFormat("EEEE")
        if (location.timeZone) {
            log.info "getDays: TimeZone is: $location.timeZone"
            df.setTimeZone(location.timeZone)
        }
        else {
            df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
        }
        def day = df.format(new Date())
        result = days.contains(day)
        log.info "getDays: Today is: $day"
        }
        return result
}
def stopPumpHandler() {
    log.debug "stopPumpHandler: Called"
    theswitch.off()	
    log.debug "stopPumpHandler: Turned off pump"
    def msgs="$howLong minute pump run is complete on"
    send(genMsg(msgs))   
    }   
    
def startPumpHandler() {
    log.debug "startPumpHandler: Called"
    def msgs="$howLong minute pump run started on"
    send(genMsg(msgs))
    theswitch.on()
    log.debug "startPumpHandler: turned on pump"
    // convert runTime minutes into seconds
    def runt = howLong * 60 
    log.info "startPumpHandler: Calling stopPumpHandler in: $howLong minutes"
    runIn(runt, stopPumpHandler)
    }
    
def genMsg(mes){    
    def now = new Date()
    def df = new java.text.SimpleDateFormat("MMMM dd yyyy hh:mm aaa")
	df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
    def day = df.format(new Date())
    return mes + " $day"
   }
    private send(msg) {
	if (sendPushMessage) {
        sendPush(msg)
    }
    if (phone) {
        sendSms(phone, msg)
    }
    log.debug "$msg"
}