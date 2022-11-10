const hlffabric = require('fabric-network');
const cahlffabric = require('fabric-ca-client');
const fs = require('fs');

function loadMemoryWallet(){
    return hlffabric.Wallets.newInMemoryWallet();
}

async function loadFileWallet(path){
    return hlffabric.Wallets.newFileSystemWallet(path);
}

async function loadCCP(path){
    const ccpJSON = (await fs.promises.readFile(path)).toString();
    return JSON.parse(ccpJSON);
}

async function createGateway(ccp, userid, wallet, chaincode, channel){
    const gateway = new hlffabric.Gateway();
    await gateway.connect(ccp, {identity: userid, wallet: wallet});
    const network = await gateway.getNetwork(channel);
    const cc = network.getContract(chaincode);
    return cc;

}

async function invokeEvaluateTransaction(cc, cmd, args){
    return await cc.evaluateTransaction(cmd, ...args);

}   

async function invokeSubmitTransactioncc(cc, cmd , args){
    return await cc.submitTransaction(cmd, ...args);
}

module.exports = {loadCCP, loadFileWallet, loadMemoryWallet, createGateway, invokeEvaluateTransaction, invokeSubmitTransactioncc};