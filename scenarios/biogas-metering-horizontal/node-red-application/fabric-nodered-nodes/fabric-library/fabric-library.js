const hlffabric = require('fabric-network');
const FabricCAServices = require("fabric-ca-client");
const fs = require('fs');

/**
 * 
 * @param {*} node 
 * @returns 
 */
function loadMemoryWallet(node){

    try {
        
        node.log('Loading memory wallet')
        return hlffabric.Wallets.newInMemoryWallet();

    } catch (error) {
        node.error(error);
    }
    
}

/**
 * 
 * @param {String} path 
 * @param {*} node 
 * @returns 
 */
async function loadFileWallet(path, node){

    if(!path) node.error("The Path is not set!");

    try {
         node.log('Loading file wallet')
         return hlffabric.Wallets.newFileSystemWallet(path);
    } catch (error) {
        node.error(error);
    }
}

/**
 * 
 * @param {String} path 
 * @param {*} node 
 * @returns 
 */
async function loadCCP(path, node){
    if(!path) node.error("The Path is not set!");

    try {
         node.log('Loading connection profile')
         const ccpJSON = (await fs.promises.readFile(path)).toString();
         return JSON.parse(ccpJSON);
    } catch (error) {
        node.error(error);
    }
}

/**
 * 
 * @param {*} ccp 
 * @param {*} userid 
 * @param {*} wallet 
 * @param {*} chaincode 
 * @param {*} channel 
 * @param {*} node 
 * @returns 
 */
async function createGateway(ccp, userid, wallet, chaincode, channel, node){

    try {
        node.log('Creating gateway')
        const gateway = new hlffabric.Gateway();
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
/**
 * 
 * @param {*} cc 
 * @param {*} cmd 
 * @param {*} args 
 * @param {*} node 
 * @returns 
 */
async function invokeEvaluateTransaction(cc, cmd, args, node){

    try {
        node.log("Invoking evaluate transaction")
        return await cc.evaluateTransaction(cmd, ...args);
    } catch (error) {
        node.error(error);
    }

}   
/**
 * 
 * @param {*} cc 
 * @param {*} cmd 
 * @param {*} args 
 * @param {*} node 
 * @returns 
 */
async function invokeSubmitTransactioncc(cc, cmd , args, node){
    try {
        node.log("Invoking submit transaction")
        return await cc.submitTransaction(cmd, ...args);
    } catch (error) {
        node.error(error);
    }
}
/**
 * 
 * @param {*} ccp 
 * @param {*} caAddress 
 * @param {*} node 
 * @returns 
 */
function createCAClient(ccp, caAddress, node){
    try {
        const caInfo = ccp.certificateAuthorities[caAddress];
        const caTLSCACerts = caInfo.tlsCACerts.pem;
        node.log("Creating ca client")
        return new FabricCAServices(caInfo.url, {trustedRoots: caTLSCACerts, verify: false}, caInfo.caName);
    } catch (error) {
        node.error(error);
    }
}

/**
 * 
 * @param {*} caClient 
 * @param {*} wallet 
 * @param {*} enrollmentId 
 * @param {*} enrollmentSecret 
 * @param {*} mspId 
 * @param {*} node 
 * @returns 
 */
async function enrollUser(caClient, wallet, enrollmentId, enrollmentSecret, mspId, node){

    const existingUser = await wallet.get(enrollmentId);

    if(existingUser){
        node.error("User already exists!")
        return;
    }

    const enrollment = await caClient.enroll({
        enrollmentID: enrollmentId,
        enrollmentSecret: enrollmentSecret 
    });

    const x509Identity = {
        credentials: {
            certificate: enrollment.certificate,
            privateKey: enrollment.key.toBytes(),
        },
        mspId: mspId,
        type: 'X.509'
    };

    await wallet.put(enrollmentId, x509Identity);

    node.log("User: "+ enrollmentId + " succesfully enrolled!");

}

/**
 * 
 * @param {*} caClient 
 * @param {*} wallet 
 * @param {*} enrollmentId 
 * @param {*} role 
 * @param {*} node 
 * @returns 
 */
async function registerUser(caClient, wallet, enrollmentId, role, node){

    const existingUser = await wallet.get(enrollmentId);
    const adminId = await wallet.get('admin');

    if(existingUser){
        node.error('Error User: ' + enrollmentId + ' already exists');
        return;
    }

    if(!'admin'){
        node.error('You are not the admin!')
        return;
    }

    const provider = wallet.getProviderRegistry().getProvider(adminId.type);
    const adminUser = await provider.getUserContext(adminId, 'admin');

    const secret = await caClient.register({
        enrollmentID: enrollmentId,
        role : role
    }, adminUser);

    return secret;
}
/**
 * 
 * @param {*} caClient 
 * @param {*} wallet 
 * @param {*} userid 
 * @param {*} password 
 * @param {*} mspId 
 * @param {*} node 
 */
async function enrollAdminUser(caClient, wallet, userid, password, mspId, node){
    await enrollUser(caClient, wallet, userid, password, mspId, node);
    node.log('Admin enrolled successfully');
}

// revoke User
function revokeUser(caClient, node){

}
    

module.exports = {loadCCP, loadFileWallet, loadMemoryWallet, createGateway, invokeEvaluateTransaction, invokeSubmitTransactioncc,
                  createCAClient, enrollAdminUser, enrollUser, registerUser};