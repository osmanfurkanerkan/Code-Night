🚀 Turkcell Game+ Quest League
Codenight Case – Görev, Puan, Rozet ve Leaderboard Sistemi

Bu proje, Turkcell Game+ Quest League için geliştirilen bir görev ve ödül motorudur.
Sistem; kullanıcı aktivitelerini okuyarak görevleri tetikler, puan kazandırır, rozet atar ve leaderboard üretir.

🎯 Amaç

Game+ kullanıcılarının oyun içi aktivitelerine göre:

Görev tamamlama

Puan kazanımı

Rozet (Badge) atama

Leaderboard üretimi

Bildirim (mock) oluşturma

işlemlerini gerçekleştiren veri tabanlı bir sistem geliştirmek.

🧠 Sistem Mimarisi

Sistem aşağıdaki temel bileşenlerden oluşur:

CSV tabanlı veri kaynakları

Türetilmiş kullanıcı metrikleri (state engine)

Quest motoru

Çakışma (priority) yönetimi

Points Ledger (puan defteri)

Leaderboard üretimi

Badge sistemi

Bildirim (mock) servisi

Web tabanlı Dashboard

📂 Veri Kaynakları

Sistem aşağıdaki CSV dosyalarından veri okur:

users.csv

games.csv

activity_events.csv

quests.csv

badges.csv

Aktivite event’leri günlük özet formatındadır.

📊 Türetilen Metrikler (User State Engine)

Belirli bir as_of_date için kullanıcı bazlı metrikler üretilir:

📅 Bugün

login_count_today

play_minutes_today

pvp_wins_today

coop_minutes_today

topup_try_today

📆 Son 7 Gün

play_minutes_7d

topup_try_7d

logins_7d

🔥 Streak

login_streak_days
Ardışık günlerde login_count >= 1 kontrol edilir.

Bu çıktılar user_state olarak tutulur.

🎮 Quest Motoru

Görevler veri tabanlıdır (quests.csv).

Her görev:

quest_id

quest_name

quest_type (DAILY, WEEKLY, STREAK)

condition

reward_points

priority

is_active

Sistem:

is_active = true görevleri değerlendirir

Koşulları sağlayan görevleri belirler

Çakışma kuralı uygular

⚖️ Çakışma Yönetimi (Tek Ödül Kuralı)

Aynı gün birden fazla görev tetiklenirse:

priority değeri en küçük olan seçilir (1 en yüksek)

Diğerleri suppressed edilir

Kullanıcıya sadece seçilen görev kadar puan verilir

Üretilen çıktı:

quest_awards

📒 Points Ledger (Puan Defteri)

Toplam puan doğrudan kullanıcı tablosuna yazılmaz.

Her puan hareketi ledger’a kaydedilir:

ledger_id

user_id

points_delta

source (QUEST_REWARD)

source_ref (award_id)

created_at

Toplam puan:

SUM(points_delta)


şeklinde türetilir.

🏆 Leaderboard

As_of_date için leaderboard üretilir:

rank

user_id

total_points

Sıralama:

total_points DESC

user_id ASC

Çıktı: leaderboard.csv

🥇 Badge Sistemi

Rozetler eşik bazlıdır:

≥ 300 → Bronz

≥ 800 → Gümüş

≥ 1500 → Altın

Koşul sağlanınca:

badge_awards çıktısı üretilir.

🔔 Bildirim Sistemi (Mock)

Görev kazanıldığında kullanıcıya mock bildirim üretilir.

Örnek:

channel: BiP

message

sent_at

🖥️ Dashboard

Web tabanlı arayüz:

Kullanıcı listesi & total_points

Leaderboard (Top 10)

Kullanıcı detay metrikleri

Triggered / Selected / Suppressed quests

Kazanılan rozetler

Bildirim kayıtları

⭐ Bonus Özellikler

Quest yönetim ekranı

What-if simülasyonu

Günlük puan grafiği (ledger bazlı)

🏗️ Teknik Yaklaşım

Bu proje:

Veri odaklı tasarım

Rule engine mantığı

Ledger pattern

Deterministic priority resolution

Modüler ve genişletilebilir yapı

ilkeleriyle geliştirilmiştir.

📌 Değerlendirme Kriterleri

Temel işlevsellik

Veri modeli

Kural motoru

Kod kalitesi

Dashboard anlatılabilirliği

Bonus özellikler

👥 Takım

Codenight kapsamında ekip çalışması olarak geliştirilmiştir.
