package gov.customs.jm.data;

// Generated Jun 4, 2015 4:42:51 PM by Hibernate Tools 3.4.0.CR1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class RuleFeedbackData.
 * @see gov.customs.jm.data.RuleFeedbackData
 * @author Hibernate Tools
 */
public class RuleFeedbackDataHome {

	private static final Log log = LogFactory
			.getLog(RuleFeedbackDataHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(RuleFeedbackData transientInstance) {
		log.debug("persisting RuleFeedbackData instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(RuleFeedbackData instance) {
		log.debug("attaching dirty RuleFeedbackData instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(RuleFeedbackData instance) {
		log.debug("attaching clean RuleFeedbackData instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(RuleFeedbackData persistentInstance) {
		log.debug("deleting RuleFeedbackData instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public RuleFeedbackData merge(RuleFeedbackData detachedInstance) {
		log.debug("merging RuleFeedbackData instance");
		try {
			RuleFeedbackData result = (RuleFeedbackData) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public RuleFeedbackData findById(gov.customs.jm.data.RuleFeedbackDataId id) {
		log.debug("getting RuleFeedbackData instance with id: " + id);
		try {
			RuleFeedbackData instance = (RuleFeedbackData) sessionFactory
					.getCurrentSession().get(
							"gov.customs.jm.data.RuleFeedbackData", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<RuleFeedbackData> findByExample(RuleFeedbackData instance) {
		log.debug("finding RuleFeedbackData instance by example");
		try {
			List<RuleFeedbackData> results = (List<RuleFeedbackData>) sessionFactory
					.getCurrentSession()
					.createCriteria("gov.customs.jm.data.RuleFeedbackData")
					.add(create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
