pkgname=androidx-watcher
license=('Apache')
arch=('any')
install=${pkgname}.install
pkgver=0
pkgrel=1
pkgver() {
    cd ${startdir}
    ${startdir}/gradlew properties -q | grep "version:" | awk '{print $2}'
}
build() {
    cd ${startdir}
    ${startdir}/gradlew pkg -q
}
package() {
    cd ${startdir}
    install -Dm 644 "androidx-watcher.service" "${pkgdir}/etc/systemd/system/androidx-watcher.service"
    install -Dm 644 "androidx-watcher.properties.example" "${pkgdir}/opt/androidx-watcher/androidx-watcher.properties.example"
    cd "build/out"
    install -Dm 644 "androidx-watcher.jar" "${pkgdir}/opt/androidx-watcher/androidx-watcher.jar"
    cd "lib"
    for jar in *.jar; do
        install -Dm 644 $jar "${pkgdir}/opt/androidx-watcher/lib/${jar}";
    done
}
