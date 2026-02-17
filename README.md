# 🚀 Turkcell Game+ Quest League  
## Codenight Case – Görev, Puan, Rozet ve Leaderboard Sistemi

Bu proje, Turkcell Game+ için geliştirilen veri tabanlı bir görev (quest) ve ödül motorudur.  
Sistem; kullanıcı aktivitelerini okuyarak görevleri tetikler, puan kazandırır, rozet atar ve leaderboard üretir.

---

## 🎯 Amaç

Game+ kullanıcılarının oyun içi aktivitelerine göre:

- Görev tamamlama
- Puan kazanımı
- Rozet (Badge) atama
- Leaderboard üretimi
- Bildirim (Mock) oluşturma
- Web tabanlı dashboard ile sonuçları gösterme

işlevlerini gerçekleştiren bir sistem geliştirmek.

---

# 🧠 Sistem Bileşenleri

Sistem aşağıdaki temel modüllerden oluşur:

- CSV tabanlı veri kaynakları
- Kullanıcı metrik hesaplama motoru (State Engine)
- Quest (Görev) motoru
- Çakışma (Priority) yönetimi
- Points Ledger (Puan Defteri)
- Leaderboard üretimi
- Badge (Rozet) sistemi
- Bildirim (Mock) servisi
- Web tabanlı Dashboard

---

# 📂 Veri Kaynakları

Sistem aşağıdaki CSV dosyalarından veri okur:

- `users.csv`
- `games.csv`
- `activity_events.csv`
- `quests.csv`
- `badges.csv`

Aktivite verileri günlük özet formatındadır.

---

# 📊 Kullanıcı Metrikleri (User State)

Belirli bir `as_of_date` için aşağıdaki metrikler hesaplanır:

## 📅 Bugün
- `login_count_today`
- `play_minutes_today`
- `pvp_wins_today`
- `coop_minutes_today`
- `topup_try_today`

## 📆 Son 7 Gün
- `play_minutes_7d`
- `topup_try_7d`
- `logins_7d`

## 🔥 Streak
- `login_streak_days`  
  (Ardışık günlerde login ≥ 1 kontrol edilir.)

Bu değerler `user_state` çıktısı olarak üretilir.

---

# 🎮 Quest Motoru

Görevler veri tabanlıdır (`quests.csv`).

Her görev şu alanlara sahiptir:

- `quest_id`
- `quest_name`
- `quest_type` (DAILY, WEEKLY, STREAK)
- `condition`
- `reward_points`
- `priority`
- `is_active`

Sistem:

1. Aktif görevleri filtreler  
2. Koşulları sağlayan görevleri belirler  
3. Çakışma kuralını uygular  

---

# ⚖️ Çakışma Yönetimi (Tek Ödül Kuralı)

Aynı gün bir kullanıcı için birden fazla görev tetiklenirse:

- Priority değeri en küçük olan görev seçilir (1 en yüksek öncelik)
- Diğer görevler suppressed olarak işaretlenir
- Kullanıcıya yalnızca seçilen görevin puanı eklenir

Üretilen çıktı: `quest_awards`

---

# 📒 Points Ledger (Puan Defteri)

Toplam puan doğrudan kullanıcı tablosuna yazılmaz.

Her puan hareketi `points_ledger` tablosuna kaydedilir:

- `ledger_id`
- `user_id`
- `points_delta`
- `source`
- `source_ref`
- `created_at`

Toplam puan:

```text
SUM(points_delta)
```

ile hesaplanır.

---

# 🏆 Leaderboard

Belirli bir tarih için leaderboard üretilir:

- `rank`
- `user_id`
- `total_points`

Sıralama kriterleri:

1. `total_points` (Azalan)
2. `user_id` (Alfabetik)

---

# 🥇 Badge Sistemi

Rozetler eşik bazlıdır:

- ≥ 300 → Bronz
- ≥ 800 → Gümüş
- ≥ 1500 → Altın

Koşul sağlandığında `badge_awards` çıktısı üretilir.

---

# 🔔 Bildirim Sistemi (Mock)

Görev kazanıldığında kullanıcıya bildirim oluşturulur:

- `notification_id`
- `user_id`
- `channel` (BiP)
- `message`
- `sent_at`

---

# 🖥️ Dashboard

Web arayüzünde:

- Kullanıcı listesi ve toplam puan
- Top 10 leaderboard
- Kullanıcı detay metrikleri
- Triggered / Selected / Suppressed quests
- Kazanılan rozetler
- Bildirim kayıtları

gösterilir.

---

# 🏗️ Teknik Yaklaşım

Bu proje aşağıdaki prensiplerle geliştirilmiştir:

- Veri odaklı tasarım
- Rule Engine yaklaşımı
- Ledger pattern kullanımı
- Deterministic priority resolution
- Modüler ve genişletilebilir mimari

---

# 👥 Takım

Bu proje Codenight kapsamında ekip çalışması olarak geliştirilmiştir.
