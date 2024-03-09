# PITReader Simulation API

This REST-API (based on express.js) simulates the intended PITReader behavior for authenticating with the frontend app and the blockchain for transactions. Node.js is required.

# Installation

1. Download the Repository
2. cd into the folder with `cd pitreader-faker`
3. run `npm install`
4. run `node index.js`
5. By default the PITReader Faker API supports 2 organisations. To add certificates for it to return, copy your local minifabric certificates (.id files) into the profiles folder, specifically the one for org0 or org1. The two folders need to keep their naming org1-example-com or org0-example-com, no matter the true name of the org in your case. Just replace the Admin.id in case your orgs have different names.

# Usage

Use these Routes with a HTTP Client (e.g. Postman) or the inbuilt Node-RED Nodes in the Node-RED backend.

## Logging in

Login route (POST):
`/login/{org_id}`

`{org_id}` must be either `org0` or `org1`, depending on the certificate you want returned.

Logs in as the specified organisation.

## Logging out

Logout Route (POST): `/logout`

Logs out the currently logged in organisation

## Getting Certificate of currently logged in Org

Transponder Route (GET): `/api/transponder`

Returns complete certificate String that is saved in the respective Admin.id file.

## Customization

- Currently no major configuration options are possible
- All the magic happens in the file index.js
- Changing the Port of the PITReader Faker from 3000 to whatever can simply be done by modifying the code there
- Adding more than 2 orgs is currently not implemented, but if you look at the code structure, it can be easily added if required for your case (This is a workaround for developers that dont have the whole environment around them, so 2 orgs mostly should be sufficient)
