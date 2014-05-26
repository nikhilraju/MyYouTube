package fileop;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.Aliases;
import com.amazonaws.services.cloudfront.model.AllowedMethods;
import com.amazonaws.services.cloudfront.model.CacheBehavior;
import com.amazonaws.services.cloudfront.model.CacheBehaviors;
import com.amazonaws.services.cloudfront.model.CloudFrontOriginAccessIdentityConfig;
import com.amazonaws.services.cloudfront.model.CookiePreference;
import com.amazonaws.services.cloudfront.model.CreateCloudFrontOriginAccessIdentityRequest;
import com.amazonaws.services.cloudfront.model.CreateCloudFrontOriginAccessIdentityResult;
import com.amazonaws.services.cloudfront.model.CreateDistributionRequest;
import com.amazonaws.services.cloudfront.model.CreateStreamingDistributionRequest;
import com.amazonaws.services.cloudfront.model.CustomOriginConfig;
import com.amazonaws.services.cloudfront.model.DefaultCacheBehavior;
import com.amazonaws.services.cloudfront.model.DistributionConfig;
import com.amazonaws.services.cloudfront.model.ForwardedValues;
import com.amazonaws.services.cloudfront.model.ListDistributionsRequest;
import com.amazonaws.services.cloudfront.model.LoggingConfig;
import com.amazonaws.services.cloudfront.model.Origin;
import com.amazonaws.services.cloudfront.model.Origins;
import com.amazonaws.services.cloudfront.model.PriceClass;
import com.amazonaws.services.cloudfront.model.S3Origin;
import com.amazonaws.services.cloudfront.model.S3OriginConfig;
import com.amazonaws.services.cloudfront.model.StreamingDistribution;
import com.amazonaws.services.cloudfront.model.StreamingDistributionConfig;
import com.amazonaws.services.cloudfront.model.StreamingLoggingConfig;
import com.amazonaws.services.cloudfront.model.TrustedSigners;
import com.amazonaws.services.s3.AmazonS3Client;

public class StartUp {

	public StartUp() throws IOException {
		AWSCredentials credentials = new PropertiesCredentials(
				StartUp.class
						.getResourceAsStream("../AwsCredentials.properties"));
		AmazonS3Client s3 = new AmazonS3Client(credentials);
		AmazonCloudFrontClient cloudfront = new AmazonCloudFrontClient(
				credentials);
		this.createBucket(s3);
		// TODO Auto-generated constructor stub
		CreateCloudFrontOriginAccessIdentityRequest or = new CreateCloudFrontOriginAccessIdentityRequest();
		or.withCloudFrontOriginAccessIdentityConfig(new CloudFrontOriginAccessIdentityConfig()
				.withCallerReference("" + System.currentTimeMillis())
				.withComment("aj2568-identity"));
		CreateCloudFrontOriginAccessIdentityResult cr = cloudfront
				.createCloudFrontOriginAccessIdentity(or);
		// Create distribution config
		// DistributionConfig dc = new DistributionConfig().
		this.createStreamingDistribution(cloudfront);
		this.createWebDistribution(cloudfront);
		// List existing CloudFront Distributions
		System.out.println("Distributions: "
				+ cloudfront.listDistributions(new ListDistributionsRequest()));
	}

	public void createBucket(AmazonS3Client s3) {
		s3.createBucket("nr2483videos");
	}

	public void createWebDistribution(AmazonCloudFrontClient cloudfront) {
		Origins o = new Origins();
		CacheBehavior cb = new CacheBehavior()
				.withAllowedMethods(
						new AllowedMethods().withItems("GET", "HEAD")
								.withQuantity(2))
				.withViewerProtocolPolicy("allow-all")
				.withPathPattern("*")
				.withMinTTL(0L)
				.withTargetOriginId("S3-nr2483videos")
				.withTrustedSigners(
						new TrustedSigners().withEnabled(false).withQuantity(0))
				.withForwardedValues(
						new ForwardedValues().withCookies(
								new CookiePreference().withForward("none"))
								.withQueryString(false));
		o.withItems(
				new Origin()
						.withS3OriginConfig(
								new S3OriginConfig()
										.withOriginAccessIdentity("aj2568-identity"))
						.withDomainName("nr2483videos.s3.amazonaws.com")
						.withId("S3-nr2483videos")).withQuantity(1);
		DistributionConfig dc = new DistributionConfig()
				.withCallerReference("" + System.currentTimeMillis())
				.withOrigins(o)
				.withDefaultRootObject("")
				.withPriceClass("PriceClass_100")
				.withEnabled(true)
				.withComment("NR2483 Distribution")
				.withDefaultCacheBehavior(
						new DefaultCacheBehavior()
								.withAllowedMethods(
										new AllowedMethods().withItems("GET",
												"HEAD").withQuantity(2))
								.withViewerProtocolPolicy("allow-all")
								.withMinTTL(0L)
								.withTargetOriginId("S3-nr2483videos")
								.withTrustedSigners(
										new TrustedSigners().withEnabled(false)
												.withQuantity(0))
								.withForwardedValues(
										new ForwardedValues().withCookies(
												new CookiePreference()
														.withForward("none"))
												.withQueryString(false)))
				.withCacheBehaviors(
						new CacheBehaviors().withItems(cb).withQuantity(1))
				.withAliases(
						new Aliases().withItems(new String[] {})
								.withQuantity(0))
				.withLogging(
						new LoggingConfig()
								.withBucket("nr2483videos.s3.amazonaws.com")
								.withEnabled(true).withPrefix("streaming_log")
								.withIncludeCookies(false));
		CreateDistributionRequest cdr = new CreateDistributionRequest()
				.withDistributionConfig(dc);
		cloudfront.createDistribution(cdr);
	}

	public void createStreamingDistribution(AmazonCloudFrontClient cloudfront) {
		StreamingDistributionConfig sdc = new StreamingDistributionConfig()
				.withS3Origin(
						new S3Origin("nr2483videos.s3.amazonaws.com")
								.withOriginAccessIdentity(
										"aj2568-identity")
								.withDomainName("nr2483videos.s3.amazonaws.com"))
				.withEnabled(true)
				.withPriceClass("PriceClass_100")
				.withComment("NR2483 Distribution")
				.withTrustedSigners(
						new TrustedSigners().withEnabled(false).withQuantity(0))
				.withAliases(
						new Aliases().withItems(new String[] {})
								.withQuantity(0))
				.withLogging(
						new StreamingLoggingConfig()
								.withBucket("nr2483videos.s3.amazonaws.com")
								.withEnabled(true).withPrefix("streaming_log"))
				.withCallerReference("" + System.currentTimeMillis());
		StreamingDistribution sd = new StreamingDistribution()
				.withStreamingDistributionConfig(sdc);
		CreateStreamingDistributionRequest sdcr = new CreateStreamingDistributionRequest(
				sdc);
		// Create a new CloudFront Distribution
		cloudfront.createStreamingDistribution(sdcr);

	}

	public static void main(String argvs[]) throws IOException {
		StartUp t = new StartUp();
	}
}
