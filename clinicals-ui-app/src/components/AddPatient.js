import React, { useState } from 'react';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Link } from 'react-router-dom';

const AddPatient = () => {
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [age, setAge] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        const patientData = {
            firstName,
            lastName,
            age: parseInt(age)
        };

        try {
            const response = await fetch('http://localhost:8080/api/patients', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(patientData)
            });

            if (response.ok) {
                const data = await response.json();
                toast.success(`Patient ${data.firstName || firstName} saved successfully!`);
                setFirstName('');
                setLastName('');
                setAge('');
            } else {
                toast.error('Error saving patient');
            }
        } catch (error) {
            toast.error('Error: ' + error.message);
        }
    };

    return (
      <div className="patient-page">
        <div className="patient-card">
          <h2 className="patient-title">Add Patient</h2>

          <form onSubmit={handleSubmit} className="patient-form">
            <label className="form-group">
              <span>First Name</span>
              <input
                type="text"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                required
              />
            </label>

            <label className="form-group">
              <span>Last Name</span>
              <input
                type="text"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                required
              />
            </label>

            <label className="form-group">
              <span>Age</span>
              <input
                type="number"
                value={age}
                onChange={(e) => setAge(e.target.value)}
                required
              />
            </label>

            <button type="submit" className="save-btn">Save Patient</button>
          </form>
          <Link to="/" className="table-link">
                    Back to Home
                  </Link>
        </div>
      </div>
    );
};

export default AddPatient;

