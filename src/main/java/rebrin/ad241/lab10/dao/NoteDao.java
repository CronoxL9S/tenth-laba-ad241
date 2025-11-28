package rebrin.ad241.lab10.dao;

import rebrin.ad241.lab10.model.Note;
import rebrin.ad241.lab10.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.List;

public class NoteDao {

    public void save(Note note) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(note);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public List<Note> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Note", Note.class).list();
        }
    }

    public List<Note> getAllActive() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Note where archived = false", Note.class).list();
        }
    }

    public List<Note> getAllArchived() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Note where archived = true", Note.class).list();
        }
    }

    public void delete(Note note) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(note);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Note findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Note.class, id);
        }
    }

    /**
     * Архівує всі нотатки, створені раніше ніж задана кількість днів від поточного моменту
     * @param days кількість днів (нотатки старіші за цей період будуть архівовані)
     * @return кількість архівованих записів
     */
    public int archiveOlderThan(int days) {
        Transaction transaction = null;
        int updatedCount = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Розрахунок порогової дати
            LocalDateTime thresholdDate = LocalDateTime.now().minusDays(days);

            // HQL-запит для масового оновлення
            String hql = "UPDATE Note SET archived = true WHERE createdAt < :threshold AND archived = false";
            Query<?> query = session.createQuery(hql);
            query.setParameter("threshold", thresholdDate);

            updatedCount = query.executeUpdate();
            transaction.commit();

            System.out.println("Архівовано нотаток: " + updatedCount);

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

        return updatedCount;
    }

    /**
     * Розархівує нотатку (переводить зі стану архіву в активний стан)
     * @param id ідентифікатор нотатки
     */
    public void unarchive(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Note note = session.get(Note.class, id);
            if (note != null) {
                note.setArchived(false);
                session.merge(note);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
