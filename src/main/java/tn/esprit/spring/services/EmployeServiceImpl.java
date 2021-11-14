
package tn.esprit.spring.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.spring.entities.Contrat;
import tn.esprit.spring.entities.Departement;
import tn.esprit.spring.entities.Employe;
import tn.esprit.spring.entities.Entreprise;
import tn.esprit.spring.entities.Mission;
import tn.esprit.spring.entities.Timesheet;
import tn.esprit.spring.repository.ContratRepository;
import tn.esprit.spring.repository.DepartementRepository;
import tn.esprit.spring.repository.EmployeRepository;
import tn.esprit.spring.repository.TimesheetRepository;

@Service
public class EmployeServiceImpl implements IEmployeService {

	@Autowired
	EmployeRepository employeRepository;
	@Autowired
	DepartementRepository deptRepoistory;
	@Autowired
	ContratRepository contratRepoistory;
	@Autowired
	TimesheetRepository timesheetRepository;
	private static final Logger logger = Logger.getLogger(EmployeServiceImpl.class);

	public int ajouterEmploye(Employe employe) {
		try {
			logger.info("je suis dans ajouterEmploye");
			logger.debug("je vais ajouter un employer");
			employeRepository.save(employe);
			logger.debug("je viens  de finir l'operation ajouterEmploye :" + employe);
			logger.info("ajouterEmploye sans erreur");
			
			return employe.getId();
		} catch (Exception e) {
			logger.error("erreur dans ajouterEmploye:"+e);
			return 0;
		}
	}

	public void mettreAjourEmailByEmployeId(String email, int employeId) {
		try {
			logger.info("je suis dans mettreAjourEmailByEmployeId");
			logger.debug("je vais modifier l'email de l'employer employer");
			Employe employe = employeRepository.findById(employeId).orElseThrow(null);
			employe.setEmail(email);
			logger.debug("je viens de finir l'operation mettreAjourEmailByEmployeId de l'employer :" + employe.getNom()
					+ " avec email " + email);
			employeRepository.save(employe);
		} catch (Exception e) {
			logger.error("erreur dans mettreAjourEmailByEmployeId:" + e);
		}
	}

	@Transactional
	public Boolean affecterEmployeADepartement(int employeId, int depId) {
		try {
			logger.info("je suis dans affecterEmployeADepartement");
			Departement depManagedEntity = deptRepoistory.findById(depId).orElseThrow(null);
			Employe employeManagedEntity = employeRepository.findById(employeId).orElseThrow(null);
			logger.debug("je vais affecter l'employe :" + employeManagedEntity.toString() + " a la departement :"
					+ depManagedEntity.toString());
			if (depManagedEntity.getEmployes() == null) {

				List<Employe> employes = new ArrayList<>();
				employes.add(employeManagedEntity);
				logger.debug("je viens de finir l'operation affecterEmployeADepartement");
				depManagedEntity.setEmployes(employes);
			} else {
				logger.debug("je viens de finir l'operation affecterEmployeADepartement");
				depManagedEntity.getEmployes().add(employeManagedEntity);

			}
			return true;
		} catch (Exception e) {
			logger.error("erreur dans affecterEmployeADepartement:" + e);
			return false;
		}

	}

	@Transactional
	public Boolean desaffecterEmployeDuDepartement(int employeId, int depId) {
		try {
			logger.info("je suis dans desaffecterEmployeDuDepartement");
			Departement dep = deptRepoistory.findById(depId).orElseThrow(null);
			logger.debug("je vais dessaffeccter l'employe  a la departement :" + dep.toString());

			int employeNb = dep.getEmployes().size();
			for (int index = 0; index < employeNb; index++) {
				if (dep.getEmployes().get(index).getId() == employeId) {
					dep.getEmployes().remove(index);
					break;// a revoir
				}
				logger.info("je viens de finir l'operation desaffecterEmployeDuDepartement");
			}
			return true;
		} catch (Exception e) {
			logger.error("erreur dans desaffecterEmployeDuDepartement:" + e);
			return false;
		}

	}

	public int ajouterContrat(Contrat contrat) {
		try {
			logger.info("je suis dans ajouterContrat");
			contratRepoistory.save(contrat);
			logger.debug("je viens de finir l'operation ajouterContrat");
			return contrat.getReference();
		} catch (Exception e) {
			logger.error("erreur dans ajouterContrat:" + e);
			return 0;
		}

	}

	public void affecterContratAEmploye(int contratId, int employeId) {
		try {
			logger.info("je suis dans affecterContratAEmploye");
			Contrat contratManagedEntity = contratRepoistory.findById(contratId).orElseThrow(null);
			Employe employeManagedEntity = employeRepository.findById(employeId).orElseThrow(null);
			contratManagedEntity.setEmploye(employeManagedEntity);
			contratRepoistory.save(contratManagedEntity);
			logger.info("je viens de finir l'operation affecterContratAEmploye");
		} catch (Exception e) {
			logger.error("erreur dans ajouterContrat:" + e);
		}

	}

	public String getEmployePrenomById(int employeId) {
		try {
			logger.info("je suis dans getEmployePrenomById");
			Employe employeManagedEntity = employeRepository.findById(employeId).orElseThrow(null);
			logger.debug("je viens de finir l'operation getEmployePrenomById");
			return employeManagedEntity.getPrenom();
		} catch (Exception e) {
			logger.error("erreur dans getEmployePrenomById:" + e);
			return null;
		}

	}

	public void deleteEmployeById(int employeId) {
		try {
			logger.info("je suis dans deleteEmployeById");
			Employe employe = employeRepository.findById(employeId).orElseThrow(null);
			// Desaffecter l'employe de tous les departements
			// c'est le bout master qui permet de mettre a jour
			// la table d'association
			for (Departement dep : employe.getDepartements()) {
				dep.getEmployes().remove(employe);
			}
			employeRepository.delete(employe);
			logger.info("je viens de finir l'operation deleteEmployeById");
		} catch (Exception e) {
			logger.error("erreur dans deleteEmployeById:" + e);
		}

	}

	public void deleteContratById(int contratId) {
		try {
			logger.info("je suis dans deleteContratById");
			Contrat contratManagedEntity = contratRepoistory.findById(contratId).orElseThrow(null);
			contratRepoistory.delete(contratManagedEntity);
			logger.info("je viens de finir l'operation deleteContratById");
		} catch (Exception e) {
			logger.error("erreur dans deleteContratById :" + e);
		}

	}

	public int getNombreEmployeJPQL() {
		try {
			logger.info("je suis dans getNombreEmployeJPQL");
			return employeRepository.countemp();
		} catch (Exception e) {
			logger.error("erreur dans getNombreEmployeJPQL :" + e);
			return 0;
		}

	}

	public List<String> getAllEmployeNamesJPQL() {
		try {
			logger.info("je suis dans getNombreEmployeJPQL");
			return employeRepository.employeNames();
		} catch (Exception e) {
			logger.error("erreur dans getAllEmployeNamesJPQL :" + e);
			return new ArrayList<>();
		}

	}

	public List<Employe> getAllEmployeByEntreprise(Entreprise entreprise) {
		try {
			logger.info("je suis dans getAllEmployeByEntreprise");
			return employeRepository.getAllEmployeByEntreprisec(entreprise);
		} catch (Exception e) {
			logger.error("erreur dans getAllEmployeByEntreprise :" + e);
			return new ArrayList<>();
		}

	}

	public void mettreAjourEmailByEmployeIdJPQL(String email, int employeId) {
		try {
			logger.info("je suis dans mettreAjourEmailByEmployeIdJPQL");
			employeRepository.mettreAjourEmailByEmployeIdJPQL(email, employeId);

		} catch (Exception e) {
			logger.error("erreur dans getAllEmployeByEntreprise :" + e);
		}

	}

	public void deleteAllContratJPQL() {
		try {
			logger.info("je suis dans deleteAllContratJPQL");
			employeRepository.deleteAllContratJPQL();
		} catch (Exception e) {
			logger.error("erreur dans deleteAllContratJPQL :" + e);
		}

	}

	public float getSalaireByEmployeIdJPQL(int employeId) {
		try {
			logger.info("je suis dans getSalaireByEmployeIdJPQL");
			logger.debug("je viens de terminer l'operation getSalaireByEmployeIdJPQL");
			return employeRepository.getSalaireByEmployeIdJPQL(employeId);
		} catch (Exception e) {
			logger.error("erreur dans getSalaireByEmployeIdJPQL :" + e);
			return 0;
		}

	}

	public Double getSalaireMoyenByDepartementId(int departementId) {
		try {
			logger.info("je suis dans getSalaireMoyenByDepartementId");
			logger.debug("je viens de terminer l'operation getSalaireMoyenByDepartementId");
			return employeRepository.getSalaireMoyenByDepartementId(departementId);
		} catch (Exception e) {
			logger.error("erreur dans getSalaireMoyenByDepartementId :" + e);
			return 0.0;
		}

	}

	public List<Timesheet> getTimesheetsByMissionAndDate(Employe employe, Mission mission, Date dateDebut,
			Date dateFin) {
		try {
			logger.info("je suis dans getTimesheetsByMissionAndDate");
			logger.debug("je viens de terminer l'operation getTimesheetsByMissionAndDate");
			return timesheetRepository.getTimesheetsByMissionAndDate(employe, mission, dateDebut, dateFin);
		} catch (Exception e) {
			logger.error("erreur dans getTimesheetsByMissionAndDate :" + e);
			return new ArrayList<>();
		}

	}

	public List<Employe> getAllEmployes() {
		
		try {
			logger.info("je suis dans getAllEmployes");
			logger.debug("je viens de terminer l'operation getAllEmployes");
			return  (List<Employe>) employeRepository.findAll();
		} catch (Exception e) {
			logger.error("erreur dans getAllEmployes :" + e);
			return new ArrayList<>();
		}

	}
	

}
