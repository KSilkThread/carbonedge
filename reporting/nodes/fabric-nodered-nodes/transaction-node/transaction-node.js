module.exports = function(RED){
    
    const hlf = require('fabric-network');
    const fs = require('fs');
    const libary = require('../fabric-libary/fabric-libary');

    function txNode(config){
        RED.nodes.createNode(this, config);
        var node = this;
    
        node.configurations = RED.nodes.getNode(config.configurations);

        if(!node.configurations){
            node.error("Configuration node not found!!");
            return;
        }

        node.userid = config.userid;
        node.chaincode = config.chaincode;
        node.channel = config.channel;
        node.ccpPath = node.configurations.ccpPath;
        node.walletPath = node.configurations.walletPath;
        node.args = config.args;
        node.cmd = config.cmd
        node.transactiontype = config.transactiontype;
        node.log("Settings loaded");

        node.on('input', async function(msg){

            if(!msg.args){
                node.send(msg);
            }

            const ccp = await libary.loadCCP(node.ccpPath, node);
            const wallet = await libary.loadFileWallet(node.walletPath, node);
            const {gateway, network, cc} = await libary.createGateway(ccp, node.userid, wallet, node.chaincode, node.channel, node);
            
            let result;

            try {
                
                if(node.transactiontype === 'evaluate'){
                    result = await libary.invokeEvaluateTransaction(cc, node.cmd, msg.args, node);
                } else {
                    result = await libary.invokeSubmitTransactioncc(cc, node.cmd, msg.args, node);
                }

                msg.payload = result.toString('utf-8');
                node.send(msg);

            } catch (error) {
                node.error(error);
            } finally {
                gateway.disconnect();
                node.log("Disconnected");
            }
            
        });
    }

    RED.nodes.registerType('txNode', txNode);

}