/**
 *  JSON
 *
 *  Copyright 2015 Jesse Newland
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
    name: "JSON API",
    namespace: "jnewland",
    author: "Jesse Newland",
    description: "A JSON API for SmartThings",
    category: "SmartThings Labs",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: true)


def installed() {
    initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

def initialize() {
    if (!state.accessToken) {
        createAccessToken()
    }
}

preferences {
    page(name: "copyConfig")
}

def copyConfig() {
    if (!state.accessToken) {
        createAccessToken()
    }
    dynamicPage(name: "copyConfig", title: "Config", install:true) {
        section("Select devices to include in the /devices API call") {
            input "switches", "capability.switch", title: "Switches", multiple: true, required: false
            input "hues", "capability.colorControl", title: "Hues", multiple: true, required: false
            input "acceleration", "capability.accelerationSensor", title: "Acceleration Sensors", multiple: true, required: false
            input "audioNotification", "capability.audioNotification", title: "Audio notification devices", multiple: true, required: false
            input "batteries", "capability.battery", title: "Batteries", multiple: true, required: false
            input "colorTempBulps", "capability.colorTemperature", title: "Color Temperature Bulbs", multiple: true, required: false
            input "contactSensors", "capability.contactSensor", title: "Contact Sensors", multiple: true, required: false
            input "energyMeters", "capability.energyMeter", title: "Energy Meters", multiple: true, required: false
            input "holdableButtons", "capability.holdableButton", title: "Holdable Buttons", multiple: true, required: false
            input "illuminance", "capability.illuminanceMeasurement", title: "Illuminance", multiple: true, required: false
            input "imageCapture", "	capability.imageCapture", title: "Image Capture Devices", multiple: true, required: false
            input "locks", "capability.lock", title: "Locks", multiple: true, required: false
            input "motionSensors", "capability.motionSensor", title: "Motion Sensors", multiple: true, required: false
            input "musicPlayer", "capability.musicPlayer", title: "Music Players", multiple: true, required: false
            input "notifications", "capability.notification", title: "Notifiers", multiple: true, required: false
            input "outlets", "capability.outlet", title: "Outlets", multiple: true, required: false
            input "polling", "capability.polling", title: "Polling", multiple: true, required: false
            input "powerMeter", "capability.powerMeter", title: "Power Meter", multiple: true, required: false
            input "presenceSensors", "capability.presenceSensor", title: "Presence Sensors", multiple: true, required: false
            input "refresh", "capability.refresh", title: "Refresh", multiple: true, required: false
            input "humidity", "capability.relativeHumidityMeasurement", title: "Humidity Sensors", multiple: true, required: false
            input "sensor", "capability.sensor", title: "Sensors", multiple: true, required: false
            input "speechRecognition", "capability.speechRecognition", title: "Speech Recognition", multiple: true, required: false
            input "speechSynthesis", "capability.speechSynthesis", title: "Speech Synthesis", multiple: true, required: false
            input "tamperAlert", "capability.tamperAlert", title: "Tamper Alert", multiple: true, required: false
            input "temperature", "capability.temperatureMeasurement", title: "Temperature", multiple: true, required: false
            input "thermostats", "capability.thermostat", title: "Thermostats", multiple: true, required: false
            input "thermostatCoolingSetpoint", "capability.thermostatCoolingSetpoint", title: "Thermostat Cooling Setpoint", multiple: true, required: false
            input "thermostatFanMode", "capability.thermostatFanMode", title: "Thermostat Fan Mode", multiple: true, required: false
            input "thermostatHeatingSetpoint", "capability.thermostatHeatingSetpoint", title: "Thermostat Heating Setpoint", multiple: true, required: false
            input "thermostatMode", "capability.thermostatMode", title: "Thermostat Mode", multiple: true, required: false
            input "thermostatOperatingState", "capability.thermostatOperatingState", title: "Thermostat Operating State", multiple: true, required: false
            input "thermostatSetPoint", "capability.thermostatSetpoint", title: "Thermostat Set Point", multiple: true, required: false
            input "threeAxis", "capability.threeAxis", title: "Three Axis", multiple: true, required: false
            input "uvIndex", "capability.ultravioletIndex", title: "UV Index", multiple: true, required: false
}

        section() {
            paragraph "View this SmartApp's configuration to use it in other places."
            href url:"https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/config?access_token=${state.accessToken}", style:"embedded", required:false, title:"Config", description:"Tap, select, copy, then click \"Done\""
        }

        section() {
            href url:"https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/devices?access_token=${state.accessToken}", style:"embedded", required:false, title:"Debug", description:"View accessories JSON"
        }
    }
}

def renderConfig() {
    def configJson = new groovy.json.JsonOutput().toJson([
        description: "JSON API",
        platforms: [
            [
                platform: "SmartThings",
                name: "SmartThings",
                app_id:        app.id,
                access_token:  state.accessToken
            ]
        ],
    ])

    def configString = new groovy.json.JsonOutput().prettyPrint(configJson)
    render contentType: "text/plain", data: configString
}

def deviceCommandMap(device, type) {
  device.supportedCommands.collectEntries { command->
      def commandUrl = "https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/${type}/${device.id}/command/${command.name}?access_token=${state.accessToken}"
      [
        (command.name): commandUrl
      ]
  }
}

def authorizedDevices() {
    [
        switches: switches,
        hues: hues,
        acceleration: acceleration,
        audioNotification: audioNotification,
        batteries: batteries,
        colorTempBulps: colorTempBulps,
        contactSensors: contactSensors,
        energyMeters: energyMeters,
        holdableButtons: holdableButtons,
        illuminance: illuminance,
        imageCapture: imageCapture,
        locks: locks,
        motionSensors: motionSensors,
        musicPlayer: musicPlayer,
        notifications: notifications,
        outlets: outlets,
        polling: polling,
        powerMeter: powerMeter,
        presenceSensors: presenceSensors,
        refresh: refresh,
        humidity: humidity,
        sensor: sensor,
        speechRecognition: speechRecognition,
        speechSynthesis: speechSynthesis,
        tamperAlert: tamperAlert,
        temperature: temperature,
        thermostats: thermostats,
        thermostatCoolingSetpoint: thermostatCoolingSetpoint,
        thermostatFanMode: thermostatFanMode,
        thermostatHeatingSetpoint: thermostatHeatingSetpoint,
        thermostatMode: thermostatMode,
        thermostatOperatingState: thermostatOperatingState,
        thermostatSetPoint: thermostatSetPoint,
        threeAxis: threeAxis,
        uvIndex: uvIndex
    ]
}

def renderDevices() {
    def deviceData = authorizedDevices().collectEntries { devices->
        [
            (devices.key): devices.value.collect { device->
                [
                    name: device.displayName,
                    commands: deviceCommandMap(device, devices.key)
                ]
            }
        ]
    }
    def deviceJson    = new groovy.json.JsonOutput().toJson(deviceData)
    def deviceString  = new groovy.json.JsonOutput().prettyPrint(deviceJson)
    render contentType: "application/json", data: deviceString
}

def deviceCommand() {
  def device  = authorizedDevices()[params.type].find { it.id == params.id }
  def command = params.command
  if (!device) {
      httpError(404, "Device not found")
  } else {
      if (params.value) {
        device."$command"(params.value)
      } else {
        device."$command"()
      }
  }
}

mappings {
    if (!params.access_token || (params.access_token && params.access_token != state.accessToken)) {
        path("/devices")                      { action: [GET: "authError"] }
        path("/config")                       { action: [GET: "authError"] }
        path("/:type/:id/command/:command")   { action: [PUT: "authError"] }
    } else {
        path("/devices")                      { action: [GET: "renderDevices"]  }
        path("/config")                       { action: [GET: "renderConfig"]  }
        path("/:type/:id/command/:command")   { action: [PUT: "deviceCommand"] }
    }
}

def authError() {
    [error: "Permission denied"]
}