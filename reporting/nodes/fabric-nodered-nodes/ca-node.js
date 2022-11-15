module.exports = function(RED){

    const libary = require('./Fabric-Libary/fabric-libary')

    function enrollAdmin(config){
        RED.nodes.createNode(this, config);
        var node = this;

        node.configurations = RED.nodes.getNode(config.configurations);
        node.ccpPath = config.ccpPath;
        node.walletpath = config.walletpath;
        node.adminid = config.adminid;
        node.adminsecret = config.adminsecret;
        node.caAddress = config.caAddress;
        node.mspId = config.mspId;
        node.log("Inputs loaded");

        node.on('input', async function(msg){
            const ccp = await libary.loadCCP(node.ccpPath, node);
            node.log("Connection profile loaded");
            const wallet = await libary.loadFileWallet(node.walletpath, node);
            node.log("File wallet loaded");
            const caClient = libary.createCAClient(ccp, node.caAddress, node);
            libary.enrollAdminUser(caClient, wallet, node.adminid, node.adminsecret, node.mspId, node);
            node.log("Admin created");
            node.send(msg);
        });
    }

    RED.nodes.registerType('enrollAdmin', enrollAdmin);

    function addUser(config){
        RED.nodes.createNode(this, config);
        var node = this;

        node.configurations = RED.nodes.getNode(config.configurations);
        node.ccpPath = config.ccpPath;
        node.walletpath = config.walletpath;
        node.caAddress = config.caAddress;
        node.enrollmentId = config.enrollmentId;
        node.role = config.role;
        node.mspId = config.mspId; 

        node.on('input', async function(msg){
            const ccp = await libary.loadCCP(node.ccpPath, node);
            node.log("Connection profile loaded");
            const wallet = await libary.loadFileWallet(node.walletpath, node);
            node.log("File wallet loaded");
            const caClient = libary.createCAClient(ccp, node.caAddress, node);
            node.log("Ca client created");
            const enrollmentSecret = libary.registerUser(caClient, wallet, node.enrollmentId, node.role, node);
            node.log("Enrollment Secret created");
            libary.enrollUser(caClient, wallet, node.enrollmentId, enrollmentSecret, node.mspId, node);
            node.log("User: "+ node.enrollmentId + " created");
            node.send(msg);
        });
    }

    RED.nodes.registerType('addUser', addUser);

}