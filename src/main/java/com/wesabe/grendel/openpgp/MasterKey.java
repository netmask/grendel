package com.wesabe.grendel.openpgp;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKey;

import java.security.NoSuchProviderException;

/**
 * A PGP master key, used for signing and verifying data. <b>Must</b> be a
 * self-certified key.
 *
 * @author coda
 */
public class MasterKey extends AbstractKey {

    protected MasterKey(PGPSecretKey secretKey) {
        super(secretKey, secretKey, SignatureType.POSITIVE_CERTIFICATION);
    }

    /**
     * Loads a master key from a {@link PGPSecretKey} instance and verifies its
     * certification.
     *
     * @param key a {@link PGPSecretKey} instance
     * @return a {@link MasterKey} instance
     * @throws CryptographicException if the key is not a self-signed master key
     */
    public static MasterKey load(PGPSecretKey key) throws CryptographicException {
        final MasterKey masterKey = new MasterKey(key);
        if (verify(masterKey)) {
            return masterKey;
        }
        throw new CryptographicException("not a self-signed master key");
    }

    private static boolean verify(MasterKey key) {
        return (key.signature != null) && key.signature.verifyCertification(key);
    }

    @Override
    public UnlockedMasterKey unlock(char[] passphrase) throws CryptographicException {
        try {
            final PGPPrivateKey privateKey = secretKey.extractPrivateKey(passphrase, "BC");
            return new UnlockedMasterKey(secretKey, privateKey);
        } catch (NoSuchProviderException e) {
            throw new CryptographicException(e);
        } catch (PGPException e) {
            throw new CryptographicException("incorrect passphrase");
        }
    }
}
