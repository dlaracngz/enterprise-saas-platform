# Enterprise SaaS Platform

Java ve Spring Boot ile geliştirdiğim bu projede, birden fazla şirketin aynı platformu kullanabildiği çok kiracılı (Multi-Tenant) bir SaaS altyapısı oluşturdum.

Sistemde her şirketin kendi kullanıcıları ve verileri bulunuyor. Amacım, ortak bir altyapı üzerinden hizmet sunarken şirketler arasındaki veri erişimini birbirinden bağımsız tutmak.

## Kullanılan Teknolojiler

- Java 25
- Spring Boot 4.1.2
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL 16.3
- JWT
- BCrypt
- Maven
- Postman

## Proje Yapısı

Projeyi Modüler Monolit (Modular Monolith) mimarisiyle geliştirdim. Uygulamanın farklı işlevlerini ayrı modüllerde ele alarak kodun daha düzenli ve yönetilebilir olmasını hedefledim.

Mevcut modüller:

- **Company:** Şirket kayıt ve yönetim işlemleri.
- **User:** Kullanıcı yönetimi ve şirket-kullanıcı ilişkileri.
- **Auth:** Giriş, kimlik doğrulama ve token işlemleri.
- **Plan:** Abonelik planlarının yönetimi (geliştirme aşamasında).
- **Subscription:** Abonelik süreçleri (henüz geliştirilmedi).

Modüllerde kullanılan temel katmanlar:

- Entity
- DTO
- Repository
- Service
- Controller

## Geliştirilen Özellikler

- Çok kiracılı (Multi-Tenant) sistem yapısı.
- Şirket bazlı veri erişim kontrolleri.
- JWT ile kimlik doğrulama.
- Rol bazlı yetkilendirme.
- PLATFORM_ADMIN, TENANT_ADMIN ve USER rolleri.
- Access Token ve Refresh Token yönetimi.
- Refresh Token'ların SHA-256 ile hash'lenerek saklanması.
- Token yenileme ve iptal işlemleri.
- Kullanıcı parolalarının BCrypt ile hash'lenmesi.
- REST API geliştirme.
- PostgreSQL veritabanı entegrasyonu.

## Güvenlik

Kimlik doğrulama ve yetkilendirme işlemlerinde Spring Security ve JWT kullandım.

Kullanıcı parolaları BCrypt ile hash'lenerek saklanıyor. Access Token ve Refresh Token mekanizmasıyla oturum yönetimi sağlanıyor. Refresh Token'lar veritabanında SHA-256 hash'leriyle tutuluyor; yenileme ve iptal işlemleri destekleniyor.

Multi-Tenant yapıda şirket bazlı erişim kontrolleri uygulanarak kullanıcıların yalnızca yetkili oldukları şirketin verilerine erişmesi sağlanıyor.

## Yapılandırma

Veritabanı bağlantısı, kullanıcı bilgileri ve JWT ayarları `src/main/resources/application.properties` dosyasında tanımlanmıştır.

Projeyi kendi bilgisayarınızda çalıştırmak için bu dosyadaki bağlantı ve güvenlik ayarlarını kendi ortamınıza göre düzenlemeniz gerekir.

## Kurulum ve Çalıştırma

### Gereksinimler

- Java 25
- Maven
- PostgreSQL 16.3

### Projeyi İndirme

```
git clone https://github.com/dlaracngz/enterprise-saas-platform.git
```

### Proje Dizinine Geçme

```
cd enterprise-saas-platform
```

### Projeyi Derleme

```
mvn clean install
```

### Uygulamayı Başlatma

```
mvn spring-boot:run
```

## Geliştirme Durumu

- Company modülü
- User modülü
- Auth modülü
- JWT kimlik doğrulama ve yetkilendirme
- Plan modülü (geliştiriliyor)
- Subscription modülü (geliştiriliyor)

Proje geliştirme aşamasındadır. Yeni modüller ve özellikler eklenerek geliştirilmeye devam edilmektedir.
