# Monitoring Contract

This contract commits measurements to the hyperledger fabric network.
In general this contract uses a data structure called MonitoringAsset to interact with the ledger. A MonitoringAsset consists of a sensor id, owner organisation, the measurement and a timestamp.
The sensor id has to be organisation wide unique string in order to identify each sensor of the organisation correctly. The organisation parameter has to be the mspid as a String of the organisation, which owns the sensor.
Measurement describes the amount of CO2 measured in a certain period of time as an integer. The timestamp describes the point in time when the MonitoringAsset was created.
Each transaction of this contract returns a JSON string as a response. Responses are formated as `{"status":<statuscode>,"response":<message>}`. Possible statuscodes are 200 for 
success or 400 for failure. The response section contains a string or a boolean.

## Installation guide

Copy the folder in the /vars/chaincode section of the minifabric directory and execute the command: 

`minifab ccup -n Monitoring -l java -v 1.0.0`

## Methods

The Contract offers the following methods to invoke transaction

An `init()` method to inizialize the contract. The method returns a success response if the contract is correctly initialized.  

A `pushMonitoringAsset(String sensorid, int data)` method to write a MonitoringAsset to the ledger.
In order to prevent data manipulation the sensorid has to match the subject section of the X.509 certificate of the invoking client. So each sensor needs an own identity in the organisation wallet.
Furthermore the ValidationContract and the CertificateContratcs have to be correctly configured and valid. If everything works correctly the methods returns the response `{"status":"200", "response":"MonitoringAsset successfully added!"}`

Example: `minifab invoke -n Monitoring -p '"sensor1","1200"'`

A `monitoringExists(String mspid, String sensorid, String timestamp)` method to check if a MonitoringAsset exists on the ledger. Returns `{"status":"200", "response":true}` if a MonitoringAsset with the parameters exists otherwise `{"status":"200", "response":false}` 

A `readMonitoringAsset(final String mspid, final String sensorid, final String timestamp)` method to get a specific MonitoringAsset as a JSON.

A `queryOrgEntries(String mspid)` method, which returns all MonitoringAssets which belong to the designated organisation.

A `querySensorEntries(String mspid, String sensorid)` method, which returns all MonitoringAssets which belong to the designated sensor. 


