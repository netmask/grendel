package com.wesabe.grendel.entities;

import org.hibernate.annotations.ForeignKey;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

import static com.google.common.base.Objects.equal;
import static com.wesabe.grendel.util.HashCode.calculate;

/**
 * A composite primary key for {@link Document}, consisting of an owner (a
 * {@link User}) and a name (a {@link String}.
 *
 * @author coda
 */
public class DocumentPK implements Serializable {
    private static final long serialVersionUID = -4514388507586009635L;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_DOCUMENT_TO_OWNER")
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Override
    public int hashCode() {
        return calculate(getClass(), name, owner);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof DocumentPK)) {
            return false;
        }

        final DocumentPK that = (DocumentPK) obj;
        return equal(name, that.name) && equal(owner, that.owner);
    }
}
