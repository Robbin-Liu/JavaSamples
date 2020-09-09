package com.ethjava;

import com.ethjava.utils.Environment;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 基于ERC20的代币
 */
public class TokenClient {

	private static Web3j web3j;

	private static Admin admin;

	private static String fromAddress = "0x0fde848fe0b52b3b20c51891c56873c65668df84";

	private static String contractAddress = "0xd8729f9070d71d6d134ce663c2a51f0c886ce38b";

	private static String emptyAddress = "0x0000000000000000000000000000000000000000";

	public static void main(String[] args) {
		web3j = Web3j.build(new HttpService(Environment.RPC_URL));
		admin = Admin.build(new HttpService(Environment.RPC_URL));
		getTokenBalance(web3j, fromAddress, contractAddress);
		System.out.println(getTokenName(web3j, contractAddress));
//		System.out.println(getTokenDecimals(web3j, contractAddress));
//		System.out.println(getTokenSymbol(web3j, contractAddress));
//		System.out.println(getTokenTotalSupply(web3j, contractAddress));
//

		sendTokenRawTransaction(fromAddress,"0xfed9a83da3c966a4ce65021f64082186f5d255af",contractAddress,BigInteger.valueOf(10) );

//		System.out.println(sendTokenTransaction(
//				fromAddress, "yzw",
//				"0x6c0f49aF552F2326DD851b68832730CB7b6C0DaF", contractAddress,
//				BigInteger.valueOf(100000)));


	}

	/**
	 * 查询代币余额
	 */
	public static BigInteger getTokenBalance(Web3j web3j, String fromAddress, String contractAddress) {

		String methodName = "balanceOf";
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();
		Address address = new Address(fromAddress);
		inputParameters.add(address);

		TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
		};
		outputParameters.add(typeReference);
		Function function = new Function(methodName, inputParameters, outputParameters);
		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);

		EthCall ethCall;
		BigInteger balanceValue = BigInteger.ZERO;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			balanceValue = (BigInteger) results.get(0).getValue();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return balanceValue;
	}

	/**
	 * 查询代币名称
	 *
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	public static String getTokenName(Web3j web3j, String contractAddress) {
		String methodName = "name";
		String name = null;
		//String fromAddr = emptyAddress;
		String fromAddr = fromAddress;
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();

		TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
		};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);

		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

		EthCall ethCall;
		try {
			//ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(web3j.ethBlockNumber().send().getBlockNumber());
			ethCall = web3j.ethCall(transaction, defaultBlockParameter).sendAsync().get();


			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			name = results.get(0).getValue().toString();
//		} catch (InterruptedException | ExecutionException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * 查询代币符号
	 *
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	public static String getTokenSymbol(Web3j web3j, String contractAddress) {
		String methodName = "symbol";
		String symbol = null;
		String fromAddr = emptyAddress;
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();

		TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
		};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);

		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

		EthCall ethCall;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			symbol = results.get(0).getValue().toString();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return symbol;
	}

	/**
	 * 查询代币精度
	 *
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	public static int getTokenDecimals(Web3j web3j, String contractAddress) {
		String methodName = "decimals";
		String fromAddr = emptyAddress;
		int decimal = 0;
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();

		TypeReference<Uint8> typeReference = new TypeReference<Uint8>() {
		};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);

		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

		EthCall ethCall;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			decimal = Integer.parseInt(results.get(0).getValue().toString());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return decimal;
	}

	/**
	 * 查询代币发行总量
	 *
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	public static BigInteger getTokenTotalSupply(Web3j web3j, String contractAddress) {
		String methodName = "totalSupply";
		String fromAddr = emptyAddress;
		BigInteger totalSupply = BigInteger.ZERO;
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();

		TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
		};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);

		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

		EthCall ethCall;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			totalSupply = (BigInteger) results.get(0).getValue();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return totalSupply;
	}

	/**
	 * 代币转账
	 */
	public static String sendTokenTransaction(String fromAddress, String password, String toAddress, String contractAddress, BigInteger amount) {
		String txHash = null;

		try {
			PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(
					fromAddress, password, BigInteger.valueOf(10)).send();
			if (personalUnlockAccount.accountUnlocked()) {
				String methodName = "transfer";
				List<Type> inputParameters = new ArrayList<>();
				List<TypeReference<?>> outputParameters = new ArrayList<>();

				Address tAddress = new Address(toAddress);

				Uint256 value = new Uint256(amount);
				inputParameters.add(tAddress);
				inputParameters.add(value);

				TypeReference<Bool> typeReference = new TypeReference<Bool>() {
				};
				outputParameters.add(typeReference);

				Function function = new Function(methodName, inputParameters, outputParameters);

				String data = FunctionEncoder.encode(function);

				EthGetTransactionCount ethGetTransactionCount = web3j
						.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).sendAsync().get();
				BigInteger nonce = ethGetTransactionCount.getTransactionCount();
				BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(5), Convert.Unit.GWEI).toBigInteger();

				Transaction transaction = Transaction.createFunctionCallTransaction(fromAddress, nonce, gasPrice,
						BigInteger.valueOf(60000), contractAddress, data);

				EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(transaction).sendAsync().get();
				txHash = ethSendTransaction.getTransactionHash();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return txHash;
	}


	/**
	 * 代币转账 RawTransaction
	 */
	public static String sendTokenRawTransaction(String fromAddress,  String toAddress, String contractAddress, BigInteger amount) {
		String txHash = null;
		//转账人私钥
		Credentials credentials = Credentials.create("65F43679840DC2312A5208DC52A92789643B354ED5F7C2B371B89EB70CC545FC");
		try {

			EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
					fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
			BigInteger nonce = ethGetTransactionCount.getTransactionCount();

			BigInteger tranValue = amount.multiply( new BigInteger("1000000000000"));

			Function function = new Function(
					"transfer",
					Arrays.asList(new Address(toAddress), new Uint256(tranValue)),
					Arrays.asList(new TypeReference<Type>() {
					}));
			String encodedFunction = FunctionEncoder.encode(function);
//			RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, Convert.toWei("18", Convert.Unit.GWEI).toBigInteger(),
//					Convert.toWei("100000", Convert.Unit.WEI).toBigInteger(), contractAddress, encodedFunction);

			BigInteger gasPrice =new BigInteger("5000000000000");
			RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice ,
					new BigInteger("210000"), contractAddress, encodedFunction);



			long chainId = 200812;
//			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//			String hexValue = Numeric.toHexString(signedMessage);
			byte[] signedMessage = TransactionEncoder.signMessage(
					rawTransaction, chainId, credentials);
			String hexValue = Numeric.toHexString(signedMessage);


			System.out.println("---transfer hexValue:"+ hexValue);

			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
			if (ethSendTransaction.hasError()) {
				System.out.println("---transfer error:"+ ethSendTransaction.getError().getMessage());

			} else {
				String transactionHash = ethSendTransaction.getTransactionHash();
				System.out.println("---Transfer transactionHash:" + transactionHash);
				txHash = transactionHash;
			}

			//------------------------------

		} catch (Exception e) {
			e.printStackTrace();
		}

		return txHash;
	}

}
