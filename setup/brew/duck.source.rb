class Duck < Formula
  homepage "https://duck.sh/"
  url "${SOURCE}"
  sha1 "${SOURCE.SHA1}"
  head "https://svn.cyberduck.io/trunk/"

  depends_on :java => [:build, "1.7"]
  depends_on :xcode => :build
  depends_on "ant" => :build
  depends_on "openssl"

  def install
    system "ant", "-Dbuild.compile.target=1.7", "-Drevision=#{version.to_str[/(\d\.\d(\.\d)?)\.(\d+)/, 3]}", "cli"
    system "install_name_tool", "-change", "/usr/lib/libcrypto.0.9.8.dylib", "/usr/local/opt/openssl/lib/libcrypto.dylib", "build/duck.bundle/Contents/Frameworks/libPrime.dylib"
    system "install_name_tool", "-change", "/usr/lib/libcrypto.0.9.8.dylib", "/usr/local/opt/openssl/lib/libcrypto.dylib", "build/duck.bundle/Contents/Frameworks/librococoa.dylib"
    libexec.install Dir["build/duck.bundle/*"]
    bin.install_symlink "#{libexec}/Contents/MacOS/duck" => "duck"
  end

  test do
    filename = (testpath/"test")
    system "#{bin}/duck", "--download", stable.url, filename
    filename.verify_checksum stable.checksum
  end
end
