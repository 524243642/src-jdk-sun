/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.security.provider.certpath;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.CertPathValidatorException;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import javax.security.auth.x500.X500Principal;
import sun.security.x509.X500Name;
import sun.security.util.Debug;

/**
 * BasicChecker is a PKIXCertPathChecker that checks the basic information
 * on a PKIX certificate, namely the signature, timestamp, and subject/issuer
 * name chaining.
 *
 * @version 	%I% %G%
 * @since	1.4
 * @author	Yassir Elley
 */
class BasicChecker extends PKIXCertPathChecker {
 
    private static final Debug debug = Debug.getInstance("certpath");
    private final PublicKey trustedPubKey;
    private final X500Principal caName;
    private final Date testDate;
    private final String sigProvider;
    private final boolean sigOnly;
    private X500Principal prevSubject;
    private PublicKey prevPubKey;

    /**
     * Constructor that initializes the input parameters.
     *
     * @param trustedPubKey the initial trusted PublicKey
     * @param caName the initial CA's name
     * @param testDate the time for which the validity of the certificate
     * should be determined
     * @param sigProvider the name of the signature provider
     * @param sigOnly true if only signature checking is to be done;
     *        if false, all checks are done
     */
    BasicChecker(PublicKey trustedPubKey, X500Principal caName,
        Date testDate, String sigProvider, boolean sigOnly)
        throws CertPathValidatorException
    {
        this.trustedPubKey = trustedPubKey;
        this.caName = caName;
        this.testDate = testDate;
        this.sigProvider = sigProvider;
        this.sigOnly = sigOnly;
        init(false);
    }

    /**
     * Initializes the internal state of the checker from parameters
     * specified in the constructor.
     */
    public void init(boolean forward) throws CertPathValidatorException {
	if (!forward) {
	    prevPubKey = trustedPubKey;
	    prevSubject = caName;
	} else {
	    throw new 
		CertPathValidatorException("forward checking not supported");
	}
    }

    public boolean isForwardCheckingSupported() {
	return false;
    }

    public Set<String> getSupportedExtensions() {
	return null;
    }

    /**
     * Performs the signature, timestamp, and subject/issuer name chaining 
     * checks on the certificate using its internal state. This method does
     * not remove any critical extensions from the Collection.
     *
     * @param cert the Certificate
     * @param unresolvedCritExts a Collection of the unresolved critical
     * extensions
     * @exception CertPathValidatorException Exception thrown if certificate
     * does not verify.
     */
    public void check(Certificate cert, Collection<String> unresolvedCritExts) 
        throws CertPathValidatorException
    {
        X509Certificate currCert = (X509Certificate) cert;

        if (!sigOnly) {
            verifyTimestamp(currCert, testDate);
            verifyNameChaining(currCert, prevSubject);
        }
        verifySignature(currCert, prevPubKey, sigProvider);
	
        updateState(currCert);
    }

    /**
     * Verifies the signature on the certificate using the previous public key
     * @param cert the Certificate
     * @param prevPubKey the previous PublicKey
     * @param sigProvider a String containing the signature provider
     * @exception CertPathValidatorException Exception thrown if certificate
     * does not verify.
     */
    private void verifySignature(X509Certificate cert, PublicKey prevPubKey,
        String sigProvider) throws CertPathValidatorException
    {
        String msg = "signature";
	if (debug != null)
	    debug.println("---checking " + msg + "...");

	try {
	    cert.verify(prevPubKey, sigProvider);
	} catch (Exception e) {
	    if (debug != null) {
	    	debug.println(e.getMessage());
	    	e.printStackTrace();
	    }
	    throw new CertPathValidatorException(msg + " check failed", e);
	}

	if (debug != null)
	    debug.println(msg + " verified.");
    }

    /**
     * Internal method to verify the timestamp on a certificate
     */
    private void verifyTimestamp(X509Certificate cert, Date date)
	throws CertPathValidatorException
    {
	String msg = "timestamp";
	if (debug != null)
	    debug.println("---checking " + msg + ":" + date.toString() + "...");

	try {
	    cert.checkValidity(date);
	} catch (Exception e) {
	    if (debug != null) {
	    	debug.println(e.getMessage());
	    	e.printStackTrace();
	    }
	    throw new CertPathValidatorException(msg + " check failed", e);
	}

	if (debug != null)
	    debug.println(msg + " verified.");
    }

    /**
     * Internal method to check that cert has a valid DN to be next in a chain
     */
    private void verifyNameChaining(X509Certificate cert, 
	X500Principal prevSubject) throws CertPathValidatorException
    {
	if (prevSubject != null) {

	    String msg = "subject/issuer name chaining";
	    if (debug != null)
	    	debug.println("---checking " + msg + "...");
	    
	    X500Principal currIssuer = cert.getIssuerX500Principal();
	    // reject null or empty issuer DNs
	    
	    if (X500Name.asX500Name(currIssuer).isEmpty()) {
		throw new CertPathValidatorException(msg + " check failed: " +
		    "empty/null issuer DN in certificate is invalid"); 
	    }

	    if (!(currIssuer.equals(prevSubject))) {
		throw new CertPathValidatorException(msg + " check failed");
	    }

	    if (debug != null)
	    	debug.println(msg + " verified.");	
	}
    }

    /**
     * Internal method to manage state information at each iteration
     */
    private void updateState(X509Certificate currCert)
        throws CertPathValidatorException
    {
        PublicKey cKey = currCert.getPublicKey();
        if (debug != null) {
            debug.println("BasicChecker.updateState issuer: " +
            	currCert.getIssuerX500Principal().toString() + "; subject: " +
                currCert.getSubjectX500Principal() + "; serial#: " +
                currCert.getSerialNumber().toString());
        }
        if (cKey instanceof DSAPublicKey &&
            ((DSAPublicKey)cKey).getParams() == null) {
            //cKey needs to inherit DSA parameters from prev key
            cKey = makeInheritedParamsKey(cKey, prevPubKey);
            if (debug != null) debug.println("BasicChecker.updateState Made " +
                                             "key with inherited params"); 
        }
        prevPubKey = cKey;
        prevSubject = currCert.getSubjectX500Principal();
    }

    /**
     * Internal method to create a new key with inherited key parameters
     *
     * @param keyValueKey key from which to obtain key value
     * @param keyParamsKey key from which to obtain key parameters
     * @return new public key having value and parameters
     * @throws CertPathValidatorException if keys are not appropriate types 
     * for this operation
     */
    static PublicKey makeInheritedParamsKey(PublicKey keyValueKey,
        PublicKey keyParamsKey) throws CertPathValidatorException 
    {
        PublicKey usableKey;
        if (!(keyValueKey instanceof DSAPublicKey) ||
            !(keyParamsKey instanceof DSAPublicKey))
            throw new CertPathValidatorException("Input key is not " +
                                                 "appropriate type for " +
                                                 "inheriting parameters");
        DSAParams params = ((DSAPublicKey)keyParamsKey).getParams();
        if (params == null)
            throw new CertPathValidatorException("Key parameters missing");
        try {
            BigInteger y = ((DSAPublicKey)keyValueKey).getY();
            KeyFactory kf = KeyFactory.getInstance("DSA");
            DSAPublicKeySpec ks = new DSAPublicKeySpec(y,
                                                       params.getP(),
                                                       params.getQ(),
                                                       params.getG());
            usableKey = kf.generatePublic(ks);
        } catch (Exception e) {
            throw new CertPathValidatorException("Unable to generate key with" +
                                                 " inherited parameters: " +
                                                 e.getMessage(), e); 
        }
        return usableKey;
    }

    /**
     * return the public key associated with the last certificate processed
     *
     * @return PublicKey the last public key processed
     */
    PublicKey getPublicKey() {
        return prevPubKey;
    }
}
