package com.ethjava;

import com.ethjava.utils.Environment;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TransactionClient {
	private static Web3j web3j;
	private static Admin admin;

	private static String fromAddress = "0x056e49a3e67819063c62ce7858ec4ee997d21d84";
	private static String toAddress = "0xfed9a83da3c966a4ce65021f64082186f5d255af";
	private static BigDecimal defaultGasPrice = BigDecimal.valueOf(5);

	//设置需要的矿工费
	private static  BigInteger GAS_PRICE = BigInteger.valueOf(6L);
	private static  BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

	public static void main(String[] args) {
		web3j = Web3j.build(new HttpService(Environment.RPC_URL));
		admin = Admin.build(new HttpService(Environment.RPC_URL));

		getBalance(fromAddress);

		//2个方法，一个 ethGetTransactionByHash  一个 ethGetTransactionReceipt ，采用第二个
		try {
			EthTransaction ethTransaction =  web3j.ethGetTransactionByHash("0x55f896daa4a6bfa38124c34750fa74ddb70d636ed216b49d20d467b62fc8897c").sendAsync().get();
			System.out.println("--------------- byhash ethTransaction="+  ethTransaction);
		}  catch (Exception e) {
			e.printStackTrace();
		}

		try {

			EthGetTransactionReceipt ethGetTransactionReceipt =  web3j.ethGetTransactionReceipt("0x55f896daa4a6bfa38124c34750fa74ddb70d636ed216b49d20d467b62fc8897c").sendAsync().get();
 			TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getResult();
			System.out.println("-------------交易单据处理器回调------receipt-"+  transactionReceipt.toString());

			if (transactionReceipt.isStatusOK()) {
				System.out.println("-------------交易成功-------");

			} else if("0x0".equals(transactionReceipt.getStatus())) {
				System.out.println("-------------交易失败-------");

			}

//			Optional<TransactionReceipt> transactionReceipt= ethGetTransactionReceipt.getTransactionReceipt();
//			System.out.println("-------------交易单据处理器回调-------"+  transactionReceipt.toString());

//			if (transactionReceipt) {
//				log.info("receive success transactionReceipt, txHash: {}", txHash);
//				coinBill.setStatus(IpbtTransactionStatus.SUCCESS.ordinal());
//				coinBillRepository.save(coinBill);
//			} else if("0x0".equals(transactionReceipt.getStatus())) {
//				log.error("receive failed transactionReceipt, txHash: {}", txHash);
//			}
//
		}  catch (Exception e) {
			e.printStackTrace();
		}

		//不是必须的 缺省值就是正确的值
		BigInteger nonce = getTransactionNonce(fromAddress);

		System.out.println("---------------nonce="+nonce);



		/*
			Transaction test = Transaction.createEthCallTransaction(
					"0x056e49a3e67819063c62ce7858ec4ee997d21d84",
					"0xfed9a83da3c966a4ce65021f64082186f5d255af", "");

			try {
				EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(test).send();
				System.out.println("---------------ethEstimateGas="+ethEstimateGas.getResult() );
			} catch (IOException e) {
				e.printStackTrace();
			}
		*/

		//gasLimit = ethEstimateGas.getAmountUsed();


//		Transaction.createContractTransaction(
//				"0x52b93c80364dc2dd4444c146d73b9836bbbb2b3f", BigInteger.ONE,
//				BigInteger.TEN, "")).send();



		//sendTransaction();
		//ethSendRawTransaction();
		/*
		String privateKey = "33a14ca3a3903fb4d84d245d09544d5db1091422e4654c689afb36c31f19dde8";
		Credentials credentials = Credentials.create(privateKey);
		ECKeyPair ecKeyPair = credentials.getEcKeyPair();
		String msg = "address:\n" + credentials.getAddress()
				+ "\nprivateKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPrivateKey())
				+ "\nPublicKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPublicKey());

		//Log.e("+++", "daoru:" + msg);
		System.out.println(msg);

		getBalance(credentials.getAddress());
		 */
	}

	/**
	 * 获取余额
	 *
	 * @param address 钱包地址
	 * @return 余额
	 */
	private static BigInteger getBalance(String address) {
		BigInteger balance = null;
		try {
			DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(web3j.ethBlockNumber().send().getBlockNumber());

			// EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
			EthGetBalance ethGetBalance = web3j.ethGetBalance(address, defaultBlockParameter).send();
			balance = ethGetBalance.getBalance();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("address " + address + " balance " + balance + "wei");
		return balance;
	}

	/**
	 * 生成一个普通交易对象
	 *
	 * @param fromAddress 放款方
	 * @param toAddress   收款方
	 * @param nonce       交易序号
	 * @param gasPrice    gas 价格
	 * @param gasLimit    gas 数量
	 * @param value       金额
	 * @return 交易对象
	 */
	private static Transaction makeTransaction(String fromAddress, String toAddress,
											   BigInteger nonce, BigInteger gasPrice,
											   BigInteger gasLimit, BigInteger value) {
		Transaction transaction;
		transaction = Transaction.createEtherTransaction(fromAddress, nonce, gasPrice, gasLimit, toAddress, value);
		return transaction;
	}

	/**
	 * 获取普通交易的gas上限
	 *
	 * @param transaction 交易对象
	 * @return gas 上限
	 */
	private static BigInteger getTransactionGasLimit(Transaction transaction) {
		BigInteger gasLimit = BigInteger.ZERO;
		try {
			EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(transaction).send();
			gasLimit = ethEstimateGas.getAmountUsed();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return gasLimit;
	}

	private static BigInteger getTransactionGasLimit2(Transaction transaction) {
		BigInteger gasLimit = BigInteger.ZERO;
		try {
			EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(transaction).send();
			gasLimit = new BigInteger(ethEstimateGas.getResult());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return gasLimit;
	}

	/**
	 * 获取账号交易次数 nonce
	 *
	 * @param address 钱包地址
	 * @return nonce
	 */
	private static BigInteger getTransactionNonce(String address) {
		BigInteger nonce = BigInteger.ZERO;
		try {
			DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(web3j.ethBlockNumber().send().getBlockNumber());

			//EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
			EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, defaultBlockParameter).send();
			nonce = ethGetTransactionCount.getTransactionCount();

			//nonce = web3j.ethGetTransactionCount(address, defaultBlockParameter)
					     //getTransactionCount
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nonce;
	}

	/**
	 * 发送一个普通交易
	 *
	 * @return 交易 Hash
	 */
	private static String sendTransaction() {
		String password = "yzw";
		BigInteger unlockDuration = BigInteger.valueOf(60L);
		BigDecimal amount = new BigDecimal("0.01");
		String txHash = null;
		try {
			PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(fromAddress, password, unlockDuration).send();
			if (personalUnlockAccount.accountUnlocked()) {
				BigInteger value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
				Transaction transaction = makeTransaction(fromAddress, toAddress,
						null, null, null, value);
				//不是必须的 可以使用默认值
				BigInteger gasLimit = getTransactionGasLimit(transaction);
				//不是必须的 缺省值就是正确的值
				BigInteger nonce = getTransactionNonce(fromAddress);
				//该值为大部分矿工可接受的gasPrice
				BigInteger gasPrice = Convert.toWei(defaultGasPrice, Convert.Unit.GWEI).toBigInteger();
				transaction = makeTransaction(fromAddress, toAddress,
						nonce, gasPrice,
						gasLimit, value);
				EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(transaction).send();
				txHash = ethSendTransaction.getTransactionHash();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("tx hash " + txHash);
		return txHash;
	}

	/**
	 * 发送一个RawTransaction交易
	 *
	 * @return 交易 Hash
	 */
	private static String ethSendRawTransaction() {

		BigDecimal amount = new BigDecimal("0.00001");
		String txHash = null;
		try {
			//转账人私钥
			Credentials credentials = Credentials.create("33a14ca3a3903fb4d84d245d09544d5db1091422e4654c689afb36c31f19dde8");
			//不是必须的 缺省值就是正确的值
			BigInteger nonce = getTransactionNonce(fromAddress);
			BigInteger value = Convert.toWei(amount, Convert.Unit.GWEI).toBigInteger();

//			Transaction estransaction = makeTransaction(fromAddress, toAddress,
//					nonce, null, null, value);

			Transaction estransaction = Transaction.createEthCallTransaction(
					fromAddress,
					toAddress, "");

 			//不是必须的 可以使用默认值
 			BigInteger gasLimit = getTransactionGasLimit2(estransaction);

			//RawTransaction rawTransaction = RawTransaction.createEtherTransaction( nonce,   GAS_PRICE, gasLimit, toAddress, value);

			RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
					nonce, GAS_PRICE, gasLimit, toAddress, value);
			long chainId = 200812;
			//签名Transaction，这里要对交易做签名
			byte[] signedMessage = TransactionEncoder.signMessage(
					rawTransaction, chainId, credentials);
			String hexValue = Numeric.toHexString(signedMessage);

			//发送交易
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
			//EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
			txHash = ethSendTransaction.getTransactionHash();

			/*
			//异步方式1
			CompletableFuture<EthSendTransaction> sendAsync = web3j.ethSendRawTransaction(hexValue).sendAsync();
			sendAsync.whenComplete((v,e)->{
				//交易回撥
				new Thread(() -> {
					//callBackService.callbackEthSendTransaction(v, logId, e);
					System.out.println("----v--getResult:"+v.getResult());
					System.out.println("----e--"+e);

				}).start();
			});

			//异步方式2
			CompletableFuture<EthSendTransaction> ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync();
			ethSendTransaction.thenAccept(transactionReceipt->{

			}).exceptionally(sendAsyncExcepiton->{

				return null;
			});
*/

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("tx hash " + txHash);
		return txHash;
	}


	//使用 web3j.ethSendRawTransaction() 发送交易 需要用私钥自签名交易 详见ColdWallet.java
}
