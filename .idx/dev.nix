{ pkgs, ... }: {
  channel = "stable-24.05"; # ou "unstable" se preferir versões mais recentes

  packages = [
    pkgs.openjdk17
    pkgs.maven
    # Adicione outros pacotes necessários aqui
  ];

  env = {
    JAVA_HOME = "${pkgs.openjdk17}";
  };

  idx = {
    extensions = [
      # Adicione extensões do VS Code conforme necessário
    ];

    previews = {
      enable = true;
      previews = {
        # web = {
        #   command = ["npm" "run" "dev"];
        #   manager = "web";
        #   env = {
        #     PORT = "$PORT";
        #   };
        # };
      };
    };

    workspace = {
      onCreate = {
        # npm-install = "npm install";
      };
      onStart = {
        # watch-backend = "npm run watch-backend";
      };
    };
  };
}