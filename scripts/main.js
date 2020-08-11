const axios = require("axios");
const { JwtGenerator } = require("virgil-sdk");
const { initCrypto, VirgilCrypto, VirgilAccessTokenSigner } = require("virgil-crypto");

const APP_ID = "YOUR_VIRGIL_APP_ID";
const APP_KEY = "YOUR_VIRGIL_APP_KEY";
const APP_KEY_ID = "YOUR_VIRGIL_APP_ID";
const PARSE_APP_ID = "YOUR_PARSE_APP_ID";
const PARSE_REST_API_KEY = "YOUR_PARSE_REST_API_KEY";

initCrypto().then(() => {
  const crypto = new VirgilCrypto();
  const generator = new JwtGenerator({
      appId: APP_ID,
      apiKeyId: APP_KEY_ID,
      apiKey: crypto.importPrivateKey(APP_KEY),
      accessTokenSigner: new VirgilAccessTokenSigner(crypto)
  });

  Parse.Cloud.define("virgil-jwt", (request) => {
    const { sessionToken } = request.params;
    return axios
      .get("https://parseapi.back4app.com/users/me", {
        headers: {
          "X-Parse-Application-Id": PARSE_APP_ID,
          "X-Parse-REST-API-Key": PARSE_REST_API_KEY,
          "X-Parse-Session-Token": sessionToken
        }
      })
      .then(resp => {
        const identity = resp.data.objectId;
        const virgilJwtToken = generator.generateToken(identity);
        const tokenStr = virgilJwtToken.toString();

        return { token: tokenStr };
      })
      .catch(error => {
        throw new Error(error.message);
      });
  });
});
