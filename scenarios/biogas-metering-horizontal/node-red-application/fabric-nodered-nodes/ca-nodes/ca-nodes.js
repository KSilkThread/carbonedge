module.exports = function(RED){

    const library = require('../fabric-library/fabric-library')

    function enrollAdmin(config){
        RED.nodes.createNode(this, config);
        var node = this;

        node.configurations = RED.nodes.getNode(config.configurations);
        node.ccpPath = node.configurations.ccpPath;
        node.walletpath = node.configurations.walletPath;
        node.adminid = config.adminid;
        node.adminsecret = config.adminsecret;
        node.caAddress = config.caAddress;
        node.mspId = config.mspId;
        node.log("Inputs loaded");

        node.on('input', async function(msg){
            const ccp = await library.loadCCP(node.ccpPath, node);
            const wallet = await library.loadFileWallet(node.walletpath, node);
            const caClient = library.createCAClient(ccp, node.caAddress, node);
            await library.enrollAdminUser(caClient, wallet, node.adminid, node.adminsecret, node.mspId, node);
            node.send(msg);
        });
    }

    RED.nodes.registerType('enrollAdmin', enrollAdmin);

    function addUser(config){
        RED.nodes.createNode(this, config);
        var node = this;

        node.configurations = RED.nodes.getNode(config.configurations);
        node.ccpPath = node.configurations.ccpPath;
        node.walletpath = node.configurations.walletPath;
        node.caAddress = config.caAddress;
        node.enrollmentId = config.enrollmentId;
        node.role = config.role;
        node.mspId = config.mspId; 

        node.on('input', async function(msg){
            const ccp = await library.loadCCP(node.ccpPath, node);
            const wallet = await library.loadFileWallet(node.walletpath, node);
            const caClient = library.createCAClient(ccp, node.caAddress, node);
            const enrollmentSecret = await library.registerUser(caClient, wallet, node.enrollmentId, node.role, node);
            await library.enrollUser(caClient, wallet, node.enrollmentId, enrollmentSecret, node.mspId, node);
            node.log("User: "+ node.enrollmentId + " created");
            node.send(msg);
        });
    }

    RED.nodes.registerType('addUser', addUser);

}