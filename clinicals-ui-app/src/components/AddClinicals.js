import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, Link } from 'react-router-dom';
import { toast } from 'react-toastify';

function AddClinicals() {
    const { patientId } = useParams();
    const [patient, setPatient] = useState(null);
    const [loading, setLoading] = useState(true);
    const [fetchError, setFetchError] = useState(null);
    const [componentName, setComponentName] = useState('');
    const [componentValue, setComponentValue] = useState('');

    useEffect(() => {
        if (patientId) {
            axios.get(`http://localhost:8080/api/patients/${patientId}`)
                .then(response => {
                    setPatient(response.data);
                    setLoading(false);
                })
                .catch(err => {
                    setFetchError(err.message);
                    setLoading(false);
                });
        }
    }, [patientId]);

    const handleSubmit = (e) => {
        e.preventDefault();

        const clinicalData = { componentName, componentValue, patientId };

        axios.post('http://localhost:8080/api/clinicaldata/clinicals', clinicalData)
            .then(() => {
                toast.success('Clinical data saved successfully!');
                setComponentName('');
                setComponentValue('');
            })
            .catch(err => {
                toast.error(err.message || 'Failed to save clinical data');
            });
    };

    return (
        <div className="patient-page">
            <div className="patient-card">
                <h2 className="patient-title">Add Clinical Data</h2>

                {loading && <p className="form-message">Loading patient...</p>}
                {fetchError && <p className="home-error">{fetchError}</p>}

                {!loading && !fetchError && patient && (
                    <>
                        <div className="patient-info">
                            <p><span>ID</span><strong>{patient.id}</strong></p>
                            <p><span>First Name</span><strong>{patient.firstName}</strong></p>
                            <p><span>Last Name</span><strong>{patient.lastName}</strong></p>
                            <p><span>Age</span><strong>{patient.age}</strong></p>
                        </div>

                        <form onSubmit={handleSubmit} className="patient-form">
                            <label className="form-group">
                                <span>Component Name</span>
                                <input
                                    type="text"
                                    value={componentName}
                                    onChange={(e) => setComponentName(e.target.value)}
                                    required
                                />
                            </label>

                            <label className="form-group">
                                <span>Component Value</span>
                                <input
                                    type="text"
                                    value={componentValue}
                                    onChange={(e) => setComponentValue(e.target.value)}
                                    required
                                />
                            </label>

                            <button type="submit" className="save-btn">Save Clinical Data</button>
                        </form>

                        <Link to="/" className="patient-card-link">
                            Back to Home
                        </Link>
                    </>
                )}
            </div>
        </div>
    );
}

export default AddClinicals;

