/** @type {import('ts-jest/dist/types').InitialOptionsTsJest} */
module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  testMatch: ['**/e2e/**/*.[jt]s?(x)', '**/?(*.)+(spec|test).[jt]s?(x)'],
}
