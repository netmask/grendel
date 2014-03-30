package com.wesabe.grendel.openpgp;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.security.SignatureException;
import java.util.List;
import java.util.Set;

import static com.wesabe.grendel.util.IntegerEquivalents.*;

/**
 * A signature on a {@link MasterKey} or {@link SubKey}.
 *
 * @author coda
 */
public class KeySignature {
    private final PGPSignature signature;
    private final PGPSignatureSubpacketVector subpackets;

    /**
     * Creates a new {@link KeySignature} given a {@link PGPSignature}.
     *
     * @param signature a {@link PGPSignature} instance
     */
    public KeySignature(PGPSignature signature) {
        this.signature = signature;
        this.subpackets = signature.getHashedSubPackets();
    }

    /**
     * Returns the type of signature {@code this} is.
     */
    public SignatureType getSignatureType() {
        return fromInt(SignatureType.class, signature.getSignatureType());
    }

    /**
     * Returns the {@link HashAlgorithm} used to make the signature.
     */
    public HashAlgorithm getHashAlgorithm() {
        return fromInt(HashAlgorithm.class, signature.getHashAlgorithm());
    }

    /**
     * Returns the timestamp at which the signature was made.
     */
    public DateTime getCreatedAt() {
        return new DateTime(signature.getCreationTime(), DateTimeZone.UTC);
    }

    /**
     * Returns the {@link AsymmetricAlgorithm} used to make the signature.
     */
    public AsymmetricAlgorithm getKeyAlgorithm() {
        return fromInt(AsymmetricAlgorithm.class, signature.getKeyAlgorithm());
    }

    /**
     * Returns the key ID of the key that made the signature.
     */
    public long getKeyID() {
        return signature.getKeyID();
    }

    /**
     * Returns the {@link KeyFlag}s asserted by the signature.
     */
    public Set<KeyFlag> getKeyFlags() {
        return fromBitmask(KeyFlag.class, subpackets.getKeyFlags());
    }

    /**
     * Returns a list of the preferred {@link SymmetricAlgorithm}s of the key.
     */
    public List<SymmetricAlgorithm> getPreferredSymmetricAlgorithms() {
        return fromIntArray(SymmetricAlgorithm.class, subpackets.getPreferredSymmetricAlgorithms());
    }

    /**
     * Returns a list of the preferred {@link CompressionAlgorithm}s of the key.
     */
    public List<CompressionAlgorithm> getPreferredCompressionAlgorithms() {
        return fromIntArray(CompressionAlgorithm.class, subpackets.getPreferredCompressionAlgorithms());
    }

    /**
     * Returns a list of the preferred {@link HashAlgorithm}s of the key.
     */
    public List<HashAlgorithm> getPreferredHashAlgorithms() {
        return fromIntArray(HashAlgorithm.class, subpackets.getPreferredHashAlgorithms());
    }

    /**
     * Verify this signature for a self-signed {@link MasterKey}.
     *
     * @param key a self-signed master key
     * @return {@code true} if the signature is valid, {@code false} otherwise
     */
    public boolean verifyCertification(MasterKey key) {
        try {
            signature.initVerify(key.getPublicKey(), "BC");
            return signature.verifyCertification(key.getUserID(), key.getPublicKey());
        } catch (PGPException | SignatureException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verify this signature for a {@link SubKey} signed by a {@link MasterKey}.
     *
     * @param key       a subkey
     * @param masterKey the signing master key
     * @return {@code true} if the signature is valid, {@code false} otherwise
     */
    public boolean verifyCertification(SubKey key, MasterKey masterKey) {
        try {
            signature.initVerify(masterKey.getPublicKey(), "BC");
            return signature.verifyCertification(masterKey.getPublicKey(), key.getPublicKey());
        } catch (Exception e) {
            return false;
        }
    }
}
