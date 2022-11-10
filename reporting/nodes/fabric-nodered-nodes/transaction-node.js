module.exports = function(RED){
    
    const hlf = require('fabric-network');
    const fs = require('fs');
    const libary = require('./Fabric-Libary/fabric-libary');

    function txNode(config){
        RED.nodes.createNode(this, config);
        var node = this;
    
        node.configurations = RED.nodes.getNode(config.configurations);

        if(!node.configurations){
            node.error("Configuration node not found!!");
            return;
        }

        node.userid = config.userid;
        node.log("ID loaded");
        node.chaincode = config.chaincode;
        node.log("Chaincode loaded");
        node.channel = config.channel;
        node.log("Channel loaded");
        node.ccpPath = node.configurations.ccpPath;
        node.log("CCPPath loaded");
        node.walletPath = node.configurations.walletPath;
        node.log("WalletPath loaded");
        node.args = config.args;
        node.params = node.args.split(' ');
        node.cmd = config.cmd
        node.transactiontype = config.transactiontype;

        node.on('input', async function(msg){

            const ccp = await libary.loadCCP(node.ccpPath);
            node.log("CCP loaded");
            const wallet = await libary.loadFileWallet(node.walletPath);
            node.log("Wallet loaded");
            const cc = await libary.createGateway(ccp, node.userid, wallet, node.chaincode, node.channel);
            node.log("Gateway created");

            let result;

            if(node.transactiontype === 'evaluate'){
                result = await libary.invokeEvaluateTransaction(cc, node.cmd, node.params);
            } else {
                console.log(node.params);
                result = await libary.invokeSubmitTransactioncc(cc, node.cmd, node.params);
            }
            
            msg.payload = result.toString('utf-8');
            node.send(msg);
            
        });
    }

    RED.nodes.registerType('txNode', txNode);

}