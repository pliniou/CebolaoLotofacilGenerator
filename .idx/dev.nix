{ pkgs, ... }: {
  channel = "stable-24.05";

  packages = [
    pkgs.openjdk17
    pkgs.maven
    pkgs.nodejs_20 # ou a versão que você quiser
    pkgs.firebase-tools
    pkgs.gradle
  ];

  env = {
    JAVA_HOME = "${pkgs.openjdk17}";
    NODE_ENV = "development";
  };

  idx = {
    workspace = {
      # Uncomment the lines below if your project is a Node.js project and
      # requires npm dependencies and/or to be served via npm.
      #
      onCreate = {
        install-deps = "npm install";
      };
      onStart = {
        serve = "firebase emulators:start";
      };
    };
  };

  idx.extensions = [
  ];
}