package otp;

public interface OTP_Strings {

	String getBalance = "https://api.tempotp.store/bal.php?key={key}";
	String getNumber = "https://api.tempotp.store/getNum.php?key={key}&service={service}";
	String getOTP = "https://api.tempotp.store/getOtp.php?id={id}&service={service}";
	String cancelNumber = "https://api.tempotp.store/cancel.php?id={id}&key={key}";
}
