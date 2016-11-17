package com.dsile.ema;

import com.google.gson.Gson;
import org.ethereum.config.SystemProperties;
import org.ethereum.config.blockchain.FrontierConfig;
import org.ethereum.core.Block;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.blockchain.SolidityContract;
import org.ethereum.util.blockchain.StandaloneBlockchain;
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.spongycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class EthereumBean {

    private StandaloneBlockchain standAlone;
    private Gson gson = new Gson();

    // Pretty simple (and probably the most expensive) Calculator
    private static final String contractSrc =
            "contract Calculator {" +
                    "  int public result;" +  // public field can be accessed by calling 'result' function
                    "  function add(int num) {" +
                    "    result = result + num;" +
                    "  }" +
                    "  function sub(int num) {" +
                    "    result = result - num;" +
                    "  }" +
                    "  function mul(int num) {" +
                    "    result = result * num;" +
                    "  }" +
                    "  function div(int num) {" +
                    "    result = result / num;" +
                    "  }" +
                    "  function clear() {" +
                    "    result = 0;" +
                    "  }" +
                    "}";

    public void start() {
        // need to modify the default Frontier settings to keep the blocks difficulty
        // low to not waste a lot of time for block mining
        SystemProperties.getDefault().setBlockchainConfig(new FrontierConfig(new FrontierConfig.FrontierConstants() {
            @Override
            public BigInteger getMINIMUM_DIFFICULTY() {
                return BigInteger.ONE;
            }
        }));

        // Creating a blockchain which generates a new block for each transaction
        // just not to call createBlock() after each call transaction
        standAlone = new StandaloneBlockchain().withAutoblock(true);
        byte[] block1 = standAlone.getBlockchain().getBestBlockHash();
        System.out.println("Creating first empty block (need some time to generate DAG)...");
        // warning up the block miner just to understand how long
        // the initial miner dataset is generated
        standAlone.createBlock();
        byte[] block2 = standAlone.getBlockchain().getBestBlockHash();
        Transaction tx = standAlone.createTransaction(0, new byte[]{1, 2, 3, 4, 5}, 300, new byte[]{1, 1, 1, 1, 1, 11, 1, 1,});
        standAlone.submitTransaction(tx);
        byte[] block3 = standAlone.getBlockchain().getBestBlockHash();
        //Transaction transaction = bc.createTransaction(key,)
        ECKey ecKey = new ECKey();
        byte[] addr = ecKey.getAddress();
        byte[] pubk = ecKey.getPubKey();

        BigInteger priv = ecKey.getPrivKey();
        ECPoint ecPubKey = ecKey.getPubKeyPoint();
        ecPubKey.getEncoded(false); //то же самое что и ecKey.getPubKey()

        ECKey ecKeyRebirth = new ECKey(priv,ecPubKey);
        ECKey ecKeyR2 = ECKey.fromPrivate(priv);
    }

    public String getBestBlock(){
        return standAlone.getBlockchain().getBestBlock().getNumber() + "";
    }

    public String addTransaction(AudioData audioData, BigInteger priv ,BigInteger addr){
        byte[] addrBytes = addr.toByteArray();
        if(isAudioDataUniq(audioData.getData())){
            Transaction tx = standAlone.createTransaction(ECKey.fromPrivate(priv), new Random().nextLong(),addrBytes,new BigInteger("10"),audioData.getData());
            standAlone.submitTransaction(tx);
            return "success";
        } else {
            return "error";
        }
    }

    private boolean isAudioDataUniq(byte[] data){
        Block currentBlock = standAlone.getBlockchain().getBestBlock();
        while(currentBlock != null){
            if(currentBlock.getTransactionsList().stream().anyMatch(t -> Arrays.equals(t.getData(),data))){
                return false;
            }
            currentBlock = standAlone.getBlockchain().getBlockByHash(currentBlock.getParentHash());
        }
        return true;
    }

    public String createNewAccount(){
        ECKey ecKey = new ECKey();
        return gson.toJson(new AccountData(new BigInteger(ecKey.getAddress()),ecKey.getPrivKey()));
    }

    private class AccountData {
        BigInteger addr;
        BigInteger priv;

        public AccountData(BigInteger addr, BigInteger priv){
            this.addr = addr;
            this.priv = priv;
        }
    }

}
