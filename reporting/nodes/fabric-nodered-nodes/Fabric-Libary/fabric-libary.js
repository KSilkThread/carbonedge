const hlffabric = require('fabric-network');
const cahlffabric = require('fabric-ca-client');
const fs = require('fs');

function loadMemoryWallet(node){
    try {
        return hlffabric.Wallets.newInMemoryWallet();
    } catch (error) {
        node.error(error);
    }
    
}

async function loadFileWallet(path, node){
    if(!path) node.error("The Path is not set!");

    try {
         return hlffabric.Wallets.newFileSystemWallet(path);
    } catch (error) {
        node.error(error);
    }
   
}

async function loadCCP(path, node){
    if(!path) node.error("The Path is not set!");
    try {
         const ccpJSON = (await fs.promises.readFile(path)).toString();
         return JSON.parse(ccpJSON);
    } catch (error) {
        node.error(error);
    }
}

async function createGateway(ccp, userid, wallet, chaincode, channel, node){

    try {const gateway = new hlffabric.Gateway();

        await gateway.connect(ccp, {identity: userid, wallet: wallet});
        const network = await gateway.getNetwork(channel);
        const cc = network.getContract(chaincode);
        return { gateway,
                 network,
                 cc     };

    } catch (error) {
        node.error(error);
    }
   
}

async function invokeEvaluateTransaction(cc, cmd, args, node){
    try {
        return await cc.evaluateTransaction(cmd, ...args);
    } catch (error) {
        node.error(error);
    }
    

}   

async function invokeSubmitTransactioncc(cc, cmd , args, node){
    try {
        return await cc.submitTransaction(cmd, ...args);
    } catch (error) {
        node.error(error);
    }
}
    

module.exports = {loadCCP, loadFileWallet, loadMemoryWallet, createGateway, invokeEvaluateTransaction, invokeSubmitTransactioncc};