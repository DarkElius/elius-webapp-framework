/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package elius.webapp.framework.security;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * An X509TrustManager implementation that trusts all certificates without validation.
 * 
 * <p><b>Warning:</b> This implementation disables SSL/TLS certificate verification 
 * and should only be used for testing or development environments.</p>
 */
public class SecurityTrustAllCertificates implements X509TrustManager {

    /**
     * Default constructor for SecurityTrustAllCertificates.
     */
    public SecurityTrustAllCertificates() {
    }

    /**
     * {@inheritDoc}
     * 
     * @param certs the peer certificate chain
     * @param authType the key exchange algorithm used
     */
    public void checkClientTrusted(X509Certificate[] certs, String authType) {
    }

    /**
     * {@inheritDoc}
     * 
     * @param certs the peer certificate chain
     * @param authType the key exchange algorithm used
     */
    public void checkServerTrusted(X509Certificate[] certs, String authType) {
    }

    /**
     * {@inheritDoc}
     * 
     * @return null, as all issuers are implicitly trusted
     */
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
