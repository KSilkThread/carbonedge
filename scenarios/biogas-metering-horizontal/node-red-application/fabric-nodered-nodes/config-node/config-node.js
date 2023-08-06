
module.exports = function(RED){

    function configNode(config){
        RED.nodes.createNode(this,config);
        this.ccpPath = config.ccpPath;
        this.walletPath = config.walletPath;
    }

    RED.nodes.registerType('configNode',configNode);
}